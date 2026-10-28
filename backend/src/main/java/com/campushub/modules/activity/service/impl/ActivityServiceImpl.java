package com.campushub.modules.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.event.EventPublisher;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.activity.entity.Activity;
import com.campushub.modules.activity.entity.ActivityCheckin;
import com.campushub.modules.activity.entity.ActivityRegistration;
import com.campushub.modules.activity.mapper.ActivityCheckinMapper;
import com.campushub.modules.activity.mapper.ActivityMapper;
import com.campushub.modules.activity.mapper.ActivityRegistrationMapper;
import com.campushub.modules.activity.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动组织服务（M4）
 * 状态机 + Redis原子计数（R7）+ 异步消息（ADR-006）
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivityRegistrationMapper registrationMapper;
    private final ActivityCheckinMapper checkinMapper;
    private final StringRedisTemplate redisTemplate;
    private final EventPublisher eventPublisher;

    private static final String COUNT_KEY_PREFIX = "activity:count:";

    @Override
    public Activity createActivity(Activity activity) {
        activity.setOrganizerId(UserContext.getUserId());
        activity.setStatus("DRAFT");
        activity.setCurrentParticipants(0);
        activity.setCreateTime(LocalDateTime.now());
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    @Transactional
    public Activity publish(Long id) {
        Activity a = getActivity(id);
        if (!"DRAFT".equals(a.getStatus())) {
            throw new BusinessException("仅草稿状态可发布");
        }
        a.setStatus("PUBLISHED");
        activityMapper.updateById(a);
        return a;
    }

    @Override
    @Transactional
    public Activity openRegister(Long id) {
        Activity a = getActivity(id);
        if (!"PUBLISHED".equals(a.getStatus()) && !"REGISTERING".equals(a.getStatus())) {
            throw new BusinessException("仅已发布状态可开放报名");
        }
        a.setStatus("REGISTERING");
        // 初始化 Redis 计数（以DB为准，幂等覆盖）
        redisTemplate.opsForValue().set(COUNT_KEY_PREFIX + id, String.valueOf(a.getCurrentParticipants()));
        activityMapper.updateById(a);
        return a;
    }

    @Override
    @Transactional
    public Activity cancel(Long id) {
        Activity a = getActivity(id);
        if ("FINISHED".equals(a.getStatus()) || "CANCELLED".equals(a.getStatus())) {
            throw new BusinessException("活动已结束或已取消，不可重复操作");
        }
        a.setStatus("CANCELLED");
        activityMapper.updateById(a);
        return a;
    }

    @Override
    @Transactional
    public Activity finish(Long id) {
        Activity a = getActivity(id);
        if (!"IN_PROGRESS".equals(a.getStatus()) && !"REGISTERING".equals(a.getStatus())) {
            throw new BusinessException("仅报名中或进行中可结束");
        }
        a.setStatus("FINISHED");
        activityMapper.updateById(a);
        return a;
    }

    @Override
    @Transactional
    public Activity updateActivity(Activity activity) {
        Activity exist = getActivity(activity.getId());
        if (!exist.getOrganizerId().equals(UserContext.getUserId()) && !UserContext.isAdmin()) {
            throw new BusinessException("仅组织者可修改活动");
        }
        activityMapper.updateById(activity);
        return getActivity(activity.getId());
    }

    @Override
    public Page<Activity> pageActivities(int page, int size, String keyword, String status) {
        LambdaQueryWrapper<Activity> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.like(Activity::getTitle, keyword);
        }
        if (StringUtils.hasText(status)) {
            qw.eq(Activity::getStatus, status);
        }
        qw.orderByDesc(Activity::getCreateTime);
        return activityMapper.selectPage(new Page<>(page, size), qw);
    }

    @Override
    public Activity getActivity(Long id) {
        Activity a = activityMapper.selectById(id);
        if (a == null) {
            throw new BusinessException("活动不存在");
        }
        return a;
    }

    @Override
    @Transactional
    public Map<String, Object> register(Long activityId) {
        Activity a = getActivity(activityId);
        // 1. 状态机校验：仅 REGISTERING 可报名
        if (!"REGISTERING".equals(a.getStatus())) {
            throw new BusinessException("活动当前不可报名（状态: " + a.getStatus() + "）");
        }
        Long userId = UserContext.getUserId();
        // 2. 一人一报（唯一键防重复）
        Long dup = registrationMapper.selectCount(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, activityId)
                        .eq(ActivityRegistration::getUserId, userId));
        if (dup > 0) {
            throw new BusinessException("您已报名该活动");
        }
        // 3. Redis 原子扣减名额（防超卖，R7）
        Long current = redisTemplate.opsForValue().increment(COUNT_KEY_PREFIX + activityId);
        if (a.getMaxParticipants() != null && current != null && current > a.getMaxParticipants()) {
            // 超卖回滚
            redisTemplate.opsForValue().decrement(COUNT_KEY_PREFIX + activityId);
            throw new BusinessException("名额已满");
        }
        // 4. 落库（以Redis计数为准，一致性由计数+唯一键保证）
        activityMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Activity>()
                        .eq("id", activityId)
                        .set("current_participants", current));
        // 5. 写报名记录
        ActivityRegistration reg = new ActivityRegistration();
        reg.setActivityId(activityId);
        reg.setUserId(userId);
        reg.setStatus("REGISTERED");
        reg.setRegisterTime(LocalDateTime.now());
        registrationMapper.insert(reg);
        // 6. 异步发布事件（通知，L7时序图）
        Map<String, Object> payload = new HashMap<>();
        payload.put("activityId", activityId);
        payload.put("activityTitle", a.getTitle());
        payload.put("userId", userId);
        eventPublisher.publish("REGISTRATION_EVENT", userId, payload);

        Map<String, Object> result = new HashMap<>();
        result.put("registrationId", reg.getId());
        result.put("activityId", activityId);
        result.put("status", "REGISTERED");
        return result;
    }

    @Override
    @Transactional
    public void cancelRegistration(Long activityId) {
        Long userId = UserContext.getUserId();
        ActivityRegistration reg = registrationMapper.selectOne(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, activityId)
                        .eq(ActivityRegistration::getUserId, userId));
        if (reg == null || !"REGISTERED".equals(reg.getStatus())) {
            throw new BusinessException("未找到有效报名记录");
        }
        reg.setStatus("CANCELLED");
        registrationMapper.updateById(reg);
        // 名额归还
        redisTemplate.opsForValue().decrement(COUNT_KEY_PREFIX + activityId);
    }

    @Override
    @Transactional
    public Map<String, Object> checkIn(Long activityId, String code) {
        Activity a = getActivity(activityId);
        if (!"IN_PROGRESS".equals(a.getStatus()) && !"REGISTERING".equals(a.getStatus())) {
            throw new BusinessException("活动未在进行中，不能签到");
        }
        Long userId = UserContext.getUserId();
        // 必须已报名
        Long regCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getActivityId, activityId)
                        .eq(ActivityRegistration::getUserId, userId)
                        .eq(ActivityRegistration::getStatus, "REGISTERED"));
        if (regCount == 0) {
            throw new BusinessException("未报名该活动，不能签到");
        }
        // 一人一次（唯一键）
        Long ckCount = checkinMapper.selectCount(
                new LambdaQueryWrapper<ActivityCheckin>()
                        .eq(ActivityCheckin::getActivityId, activityId)
                        .eq(ActivityCheckin::getUserId, userId));
        if (ckCount > 0) {
            throw new BusinessException("已签到，请勿重复");
        }
        ActivityCheckin ck = new ActivityCheckin();
        ck.setActivityId(activityId);
        ck.setUserId(userId);
        ck.setCheckinTime(LocalDateTime.now());
        ck.setCheckinMethod(StringUtils.hasText(code) ? "QR_CODE" : "MANUAL");
        checkinMapper.insert(ck);
        // 更新报名状态为 CHECKED_IN
        registrationMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<ActivityRegistration>()
                        .eq("activity_id", activityId)
                        .eq("user_id", userId)
                        .set("status", "CHECKED_IN")
                        .set("check_in_time", LocalDateTime.now()));
        Map<String, Object> result = new HashMap<>();
        result.put("checkinId", ck.getId());
        result.put("activityId", activityId);
        result.put("status", "CHECKED_IN");
        result.put("checkinTime", ck.getCheckinTime());
        return result;
    }

    @Override
    public List<ActivityRegistration> myRegistrations(Long userId) {
        return registrationMapper.selectList(
                new LambdaQueryWrapper<ActivityRegistration>()
                        .eq(ActivityRegistration::getUserId, userId)
                        .orderByDesc(ActivityRegistration::getRegisterTime));
    }

    @Override
    public List<Activity> myActivities(Long userId) {
        return activityMapper.selectList(
                new LambdaQueryWrapper<Activity>()
                        .eq(Activity::getOrganizerId, userId)
                        .orderByDesc(Activity::getCreateTime));
    }

    @Override
    public void updateStatusByTime() {
        LocalDateTime now = LocalDateTime.now();
        List<Activity> all = activityMapper.selectList(null);
        for (Activity a : all) {
            String target = null;
            if ("REGISTERING".equals(a.getStatus())
                    && a.getStartTime() != null && !now.isBefore(a.getStartTime())) {
                target = "IN_PROGRESS";
            }
            if ("IN_PROGRESS".equals(a.getStatus())
                    && a.getEndTime() != null && now.isAfter(a.getEndTime())) {
                target = "FINISHED";
            }
            if (target != null) {
                a.setStatus(target);
                activityMapper.updateById(a);
            }
        }
    }
}

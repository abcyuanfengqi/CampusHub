package com.campushub.modules.stat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campushub.modules.activity.mapper.ActivityMapper;
import com.campushub.modules.club.mapper.ClubMapper;
import com.campushub.modules.secondhand.mapper.SecondhandItemMapper;
import com.campushub.modules.stat.entity.StatDaily;
import com.campushub.modules.stat.mapper.StatDailyMapper;
import com.campushub.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据看板服务（M8）
 * 实时聚合 + 每日快照（stat_daily）
 */
@Service
@RequiredArgsConstructor
public class StatServiceImpl {

    private final StatDailyMapper statDailyMapper;
    private final UserMapper userMapper;
    private final ClubMapper clubMapper;
    private final ActivityMapper activityMapper;
    private final SecondhandItemMapper secondhandItemMapper;

    /** 生成某日统计（定时任务调用，uk_date 幂等） */
    @Transactional
    public void generateDailyStat(LocalDate date) {
        LocalDate next = date.plusDays(1);
        StatDaily sd = new StatDaily();
        sd.setStatDate(date);
        sd.setNewUsers(Math.toIntExact(userMapper.selectCount(
                new LambdaQueryWrapper<com.campushub.modules.user.entity.User>()
                        .ge(com.campushub.modules.user.entity.User::getCreateTime, date.atStartOfDay())
                        .lt(com.campushub.modules.user.entity.User::getCreateTime, next.atStartOfDay()))));
        sd.setNewActivities(Math.toIntExact(activityMapper.selectCount(
                new LambdaQueryWrapper<com.campushub.modules.activity.entity.Activity>()
                        .ge(com.campushub.modules.activity.entity.Activity::getCreateTime, date.atStartOfDay())
                        .lt(com.campushub.modules.activity.entity.Activity::getCreateTime, next.atStartOfDay()))));
        sd.setNewSecondhand(Math.toIntExact(secondhandItemMapper.selectCount(
                new LambdaQueryWrapper<com.campushub.modules.secondhand.entity.SecondhandItem>()
                        .ge(com.campushub.modules.secondhand.entity.SecondhandItem::getCreateTime, date.atStartOfDay())
                        .lt(com.campushub.modules.secondhand.entity.SecondhandItem::getCreateTime, next.atStartOfDay()))));
        sd.setNewClubs(Math.toIntExact(clubMapper.selectCount(
                new LambdaQueryWrapper<com.campushub.modules.club.entity.Club>()
                        .ge(com.campushub.modules.club.entity.Club::getCreateTime, date.atStartOfDay())
                        .lt(com.campushub.modules.club.entity.Club::getCreateTime, next.atStartOfDay()))));
        // 幂等：存在则更新，不存在插入
        StatDaily exist = statDailyMapper.selectOne(
                new LambdaQueryWrapper<StatDaily>().eq(StatDaily::getStatDate, date));
        if (exist != null) {
            sd.setId(exist.getId());
            statDailyMapper.updateById(sd);
        } else {
            statDailyMapper.insert(sd);
        }
    }

    /** 看板聚合数据 */
    public Map<String, Object> dashboard(int days) {
        Map<String, Object> result = new HashMap<>();
        // 用户统计
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("total", userMapper.selectCount(null));
        List<Map<String, Object>> userTrend = new ArrayList<>();
        List<StatDaily> stats = statDailyMapper.selectList(
                new LambdaQueryWrapper<StatDaily>()
                        .orderByAsc(StatDaily::getStatDate)
                        .last("LIMIT " + Math.max(days, 7)));
        for (StatDaily s : stats) {
            Map<String, Object> p = new HashMap<>();
            p.put("date", s.getStatDate().toString());
            p.put("newUsers", s.getNewUsers());
            p.put("newActivities", s.getNewActivities());
            p.put("newClubs", s.getNewClubs());
            p.put("newSecondhand", s.getNewSecondhand());
            userTrend.add(p);
        }
        userStats.put("trend", userTrend);
        result.put("userStats", userStats);
        // 社团
        result.put("clubTotal", clubMapper.selectCount(null));
        // 活动
        result.put("activityTotal", activityMapper.selectCount(null));
        // 二手
        result.put("secondhandTotal", secondhandItemMapper.selectCount(null));
        return result;
    }
}

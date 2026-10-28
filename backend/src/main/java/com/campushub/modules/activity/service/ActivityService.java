package com.campushub.modules.activity.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.modules.activity.entity.Activity;
import com.campushub.modules.activity.entity.ActivityCheckin;
import com.campushub.modules.activity.entity.ActivityRegistration;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    Activity createActivity(Activity activity);      // DRAFT
    Activity publish(Long id);                       // DRAFT → PUBLISHED → REGISTERING
    Activity openRegister(Long id);                  // → REGISTERING
    Activity cancel(Long id);                        // 任意态 → CANCELLED
    Activity finish(Long id);                        // → FINISHED
    Activity updateActivity(Activity activity);
    Page<Activity> pageActivities(int page, int size, String keyword, String status);
    Activity getActivity(Long id);
    /** 活动报名：状态校验 + Redis原子扣减 + DB乐观回写 + 异步通知（L7时序图） */
    Map<String, Object> register(Long activityId);
    void cancelRegistration(Long activityId);
    /** 签到核销 */
    Map<String, Object> checkIn(Long activityId, String code);
    List<ActivityRegistration> myRegistrations(Long userId);
    List<Activity> myActivities(Long userId);        // 我发布的
    void updateStatusByTime();                        // 定时推进状态机
}

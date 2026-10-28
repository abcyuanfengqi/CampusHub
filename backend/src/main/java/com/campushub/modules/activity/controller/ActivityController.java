package com.campushub.modules.activity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.result.Result;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.activity.entity.Activity;
import com.campushub.modules.activity.entity.ActivityRegistration;
import com.campushub.modules.activity.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 活动组织接口（M4）
 */
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /** 创建活动（DRAFT） */
    @PostMapping
    public Result<Activity> create(@RequestBody Activity activity) {
        return Result.success(activityService.createActivity(activity));
    }

    /** 发布（DRAFT→PUBLISHED） */
    @PutMapping("/{id}/publish")
    public Result<Activity> publish(@PathVariable Long id) {
        return Result.success(activityService.publish(id));
    }

    /** 开放报名（→REGISTERING，初始化Redis计数） */
    @PutMapping("/{id}/open")
    public Result<Activity> open(@PathVariable Long id) {
        return Result.success(activityService.openRegister(id));
    }

    /** 结束（→FINISHED） */
    @PutMapping("/{id}/finish")
    public Result<Activity> finish(@PathVariable Long id) {
        return Result.success(activityService.finish(id));
    }

    /** 取消（→CANCELLED） */
    @PutMapping("/{id}/cancel")
    public Result<Activity> cancel(@PathVariable Long id) {
        return Result.success(activityService.cancel(id));
    }

    /** 更新活动 */
    @PutMapping
    public Result<Activity> update(@RequestBody Activity activity) {
        return Result.success(activityService.updateActivity(activity));
    }

    /** 分页查询 */
    @GetMapping
    public Result<Page<Activity>> page(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String status) {
        return Result.success(activityService.pageActivities(page, size, keyword, status));
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<Activity> detail(@PathVariable Long id) {
        return Result.success(activityService.getActivity(id));
    }

    /** 活动报名（Redis原子扣减+异步通知） */
    @PostMapping("/{id}/register")
    public Result<Map<String, Object>> register(@PathVariable Long id) {
        return Result.success(activityService.register(id));
    }

    /** 取消报名（名额归还） */
    @DeleteMapping("/{id}/register")
    public Result<Void> cancelRegister(@PathVariable Long id) {
        activityService.cancelRegistration(id);
        return Result.success();
    }

    /** 签到核销（一人一次） */
    @PostMapping("/{id}/checkin")
    public Result<Map<String, Object>> checkin(@PathVariable Long id,
                                               @RequestParam(required = false) String code) {
        return Result.success(activityService.checkIn(id, code));
    }

    /** 我的报名记录 */
    @GetMapping("/registrations/mine")
    public Result<List<ActivityRegistration>> myRegistrations() {
        return Result.success(activityService.myRegistrations(UserContext.getUserId()));
    }

    /** 我发布的活动 */
    @GetMapping("/mine")
    public Result<List<Activity>> mine() {
        return Result.success(activityService.myActivities(UserContext.getUserId()));
    }
}

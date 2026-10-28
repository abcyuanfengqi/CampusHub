package com.campushub.modules.notify.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.common.annotation.RequireRole;
import com.campushub.common.result.Result;
import com.campushub.common.utils.UserContext;
import com.campushub.modules.notify.entity.Announcement;
import com.campushub.modules.notify.entity.Notification;
import com.campushub.modules.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信息推送接口（M5）
 */
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotifyController {

    private final NotifyService notifyService;

    /** 我的消息（分页） */
    @GetMapping
    public Result<Page<Notification>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return Result.success(notifyService.pageMine(UserContext.getUserId(), page, size));
    }

    /** 未读数量 */
    @GetMapping("/unread")
    public Result<Map<String, Object>> unread() {
        Map<String, Object> map = new HashMap<>();
        map.put("count", notifyService.countUnread(UserContext.getUserId()));
        return Result.success(map);
    }

    /** 标记已读 */
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        notifyService.markRead(id, UserContext.getUserId());
        return Result.success();
    }

    /** 全部已读 */
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        notifyService.markAllRead(UserContext.getUserId());
        return Result.success();
    }

    /** 发布公告（管理员） */
    @PostMapping("/announcement")
    @RequireRole("ADMIN")
    public Result<Announcement> publish(@RequestBody Announcement a) {
        a.setPublisherId(UserContext.getUserId());
        return Result.success(notifyService.publishAnnouncement(a));
    }

    /** 最新公告 */
    @GetMapping("/announcement")
    public Result<List<Announcement>> announcements(@RequestParam(defaultValue = "5") int limit) {
        return Result.success(notifyService.latestAnnouncements(limit));
    }
}

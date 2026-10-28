package com.campushub.modules.notify.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.modules.notify.entity.Announcement;
import com.campushub.modules.notify.entity.Notification;

import java.util.List;

public interface NotifyService {
    void send(Long userId, String title, String content, String type, Long refId);
    void sendToUserList(List<Long> userIds, String title, String content, String type, Long refId);
    Page<Notification> pageMine(Long userId, int page, int size);
    long countUnread(Long userId);
    void markRead(Long notificationId, Long userId);
    void markAllRead(Long userId);
    Announcement publishAnnouncement(Announcement a);
    List<Announcement> latestAnnouncements(int limit);
}

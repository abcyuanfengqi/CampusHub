package com.campushub.modules.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campushub.modules.notify.entity.Announcement;
import com.campushub.modules.notify.entity.Notification;
import com.campushub.modules.notify.mapper.AnnouncementMapper;
import com.campushub.modules.notify.mapper.NotificationMapper;
import com.campushub.modules.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final NotificationMapper notificationMapper;
    private final AnnouncementMapper announcementMapper;

    @Override
    @Transactional
    public void send(Long userId, String title, String content, String type, Long refId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setContent(content);
        n.setType(type);
        n.setRefId(refId);
        n.setIsRead(0);
        n.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(n);
    }

    @Override
    @Transactional
    public void sendToUserList(List<Long> userIds, String title, String content, String type, Long refId) {
        for (Long userId : userIds) {
            send(userId, title, content, type, refId);
        }
    }

    @Override
    public Page<Notification> pageMine(Long userId, int page, int size) {
        return notificationMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreateTime));
    }

    @Override
    public long countUnread(Long userId) {
        return notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0));
    }

    @Override
    @Transactional
    public void markRead(Long notificationId, Long userId) {
        notificationMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Notification>()
                        .eq("id", notificationId)
                        .eq("user_id", userId)
                        .set("is_read", 1));
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        notificationMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Notification>()
                        .eq("user_id", userId)
                        .eq("is_read", 0)
                        .set("is_read", 1));
    }

    @Override
    public Announcement publishAnnouncement(Announcement a) {
        a.setStatus("PUBLISHED");
        a.setCreateTime(LocalDateTime.now());
        announcementMapper.insert(a);
        return a;
    }

    @Override
    public List<Announcement> latestAnnouncements(int limit) {
        return announcementMapper.selectList(
                new LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getStatus, "PUBLISHED")
                        .orderByDesc(Announcement::getCreateTime)
                        .last("LIMIT " + limit));
    }
}

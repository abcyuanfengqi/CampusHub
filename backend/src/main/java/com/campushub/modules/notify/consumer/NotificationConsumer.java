package com.campushub.modules.notify.consumer;

import com.campushub.common.event.DomainEvent;
import com.campushub.modules.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息消费者（ADR-006）
 * 消费领域事件 → 写站内消息；失败重试3次后进死信队列
 * 幂等：以 eventId 去重（内存幂等表，正式可落库）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotifyService notifyService;
    private final ConcurrentHashMap<String, Boolean> consumedEvents = new ConcurrentHashMap<>();

    @RabbitListener(queues = "queue.campus.registration")
    public void onRegistration(DomainEvent event) {
        process(event);
        log.info("处理活动报名事件: eventId={}", event.getEventId());
    }

    @RabbitListener(queues = "queue.campus.club")
    public void onClubApproval(DomainEvent event) {
        process(event);
        log.info("处理社团审批事件: eventId={}", event.getEventId());
    }

    @RabbitListener(queues = "queue.campus.trade")
    public void onTrade(DomainEvent event) {
        process(event);
        log.info("处理二手成交事件: eventId={}", event.getEventId());
    }

    private void process(DomainEvent event) {
        // 幂等校验
        if (consumedEvents.putIfAbsent(event.getEventId(), Boolean.TRUE) != null) {
            log.warn("重复事件已消费，跳过: {}", event.getEventId());
            return;
        }
        Map<String, Object> p = event.getPayload();
        switch (event.getType()) {
            case "REGISTRATION_EVENT" -> {
                Long userId = (Long) p.get("userId");
                notifyService.send(userId, "报名成功",
                        "您已成功报名活动《" + p.get("activityTitle") + "》，请准时参加",
                        "ACTIVITY_REMIND", (Long) p.get("activityId"));
            }
            case "CLUB_APPROVED_EVENT" -> {
                Long leaderId = (Long) p.get("leaderId");
                String result = (String) p.get("result");
                String reason = p.get("reason") == null ? "" : p.get("reason").toString();
                notifyService.send(leaderId, "社团审批结果",
                        "您的社团《" + p.get("clubName") + "》已" + ("APPROVED".equals(result) ? "通过审批" : "被驳回" + reason),
                        "CLUB_APPROVAL", (Long) p.get("clubId"));
            }
            case "TRADE_EVENT" -> {
                notifyService.send((Long) p.get("sellerId"), "二手成交",
                        "您发布的《" + p.get("title") + "》已成交", "TRADE", (Long) p.get("itemId"));
                notifyService.send((Long) p.get("buyerId"), "交易成功",
                        "您购买的《" + p.get("title") + "》已确认成交，请与卖家联系", "TRADE", (Long) p.get("itemId"));
            }
            default -> log.warn("未知事件类型: {}", event.getType());
        }
    }
}

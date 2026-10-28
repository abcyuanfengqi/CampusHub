package com.campushub.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 领域事件（跨模块异步消息载体，ADR-006）
 * 通过 RabbitMQ 发布：活动报名/社团审批/二手成交
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainEvent implements Serializable {

    private String eventId;          // 事件唯一ID（消费幂等）
    private String type;             // REGISTRATION_EVENT / CLUB_APPROVED_EVENT / TRADE_EVENT
    private Long sourceUserId;       // 触发事件的用户
    private Map<String, Object> payload; // 事件载荷
    private LocalDateTime occurredAt;    // 发生时间

    public static DomainEvent of(String eventId, String type, Long sourceUserId, Map<String, Object> payload) {
        return new DomainEvent(eventId, type, sourceUserId, payload, LocalDateTime.now());
    }
}

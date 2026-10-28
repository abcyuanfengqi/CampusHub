package com.campushub.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 领域事件发布器（ADR-006）
 * 业务事务提交后调用，向 campushub.exchange 发送事件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    public static final String EXCHANGE = "campushub.exchange";
    public static final String ROUTE_REGISTRATION = "campushub.registration";
    public static final String ROUTE_CLUB = "campushub.club";
    public static final String ROUTE_TRADE = "campushub.trade";

    private final RabbitTemplate rabbitTemplate;

    public void publish(String type, Long sourceUserId, java.util.Map<String, Object> payload) {
        String route = switch (type) {
            case "REGISTRATION_EVENT" -> ROUTE_REGISTRATION;
            case "CLUB_APPROVED_EVENT" -> ROUTE_CLUB;
            case "TRADE_EVENT" -> ROUTE_TRADE;
            default -> ROUTE_REGISTRATION;
        };
        DomainEvent event = DomainEvent.of(UUID.randomUUID().toString(), type, sourceUserId, payload);
        try {
            rabbitTemplate.convertAndSend(EXCHANGE, route, event);
            log.info("事件已发布: type={}, route={}, eventId={}", type, route, event.getEventId());
        } catch (Exception e) {
            // 异步通知失败不影响主流程（降级：记日志，稍后可重发）
            log.error("事件发布失败: type={}, error={}", type, e.getMessage(), e);
        }
    }
}

package com.campushub.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置（ADR-006）
 * Direct 交换机 + 3 个业务队列 + 死信队列（重试3次后进入）
 */
@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "campushub.exchange";
    public static final String DLX = "campushub.dlx";
    public static final String QUEUE_REGISTRATION = "queue.campus.registration";
    public static final String QUEUE_CLUB = "queue.campus.club";
    public static final String QUEUE_TRADE = "queue.campus.trade";
    public static final String QUEUE_DLX = "queue.campus.dlx";

    @Bean
    public DirectExchange campushubExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return ExchangeBuilder.directExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue registrationQueue() {
        return QueueBuilder.durable(QUEUE_REGISTRATION)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey("dead")
                .build();
    }

    @Bean
    public Queue clubQueue() {
        return QueueBuilder.durable(QUEUE_CLUB)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey("dead")
                .build();
    }

    @Bean
    public Queue tradeQueue() {
        return QueueBuilder.durable(QUEUE_TRADE)
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey("dead")
                .build();
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(QUEUE_DLX).build();
    }

    @Bean
    public Binding registrationBinding() {
        return BindingBuilder.bind(registrationQueue()).to(campushubExchange()).with("campushub.registration");
    }

    @Bean
    public Binding clubBinding() {
        return BindingBuilder.bind(clubQueue()).to(campushubExchange()).with("campushub.club");
    }

    @Bean
    public Binding tradeBinding() {
        return BindingBuilder.bind(tradeQueue()).to(campushubExchange()).with("campushub.trade");
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with("dead");
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter());
        return template;
    }
}

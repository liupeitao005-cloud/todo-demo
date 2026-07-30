package com.todo.mq;

import com.todo.config.MqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqTestProducer {
    private final RabbitTemplate rabbitTemplate;
    private final MqProperties properties;

    public void send(String message) {
        MqProperties.Destination test = properties.getTest();
        rabbitTemplate.convertAndSend(test.getExchange(), test.getRoutingKey(), message);
    }
}

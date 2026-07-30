package com.todo.mq;

import com.todo.config.MqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReminderProducer {
    private final RabbitTemplate rabbitTemplate;
    private final MqProperties properties;

    public void send(Long reminderId) {
        MqProperties.Destination reminder = properties.getReminder();
        rabbitTemplate.convertAndSend(reminder.getExchange(), reminder.getRoutingKey(), reminderId);
    }
}

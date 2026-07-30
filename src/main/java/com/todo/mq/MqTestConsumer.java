package com.todo.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqTestConsumer {
    @RabbitListener(queues = "${todo.mq.test.queue}")
    public void receive(String message) {
        log.info("Received RabbitMQ test message: {}", message);
    }
}

package com.todo.mq;

import com.todo.service.TodoReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderConsumer {
    private final TodoReminderService todoReminderService;

    @RabbitListener(queues = "${todo.mq.reminder.queue}")
    public void receive(Long reminderId) {
        boolean processed = todoReminderService.processServerReminderById(reminderId);
        log.info("Consumed reminder message. reminderId={}, processed={}", reminderId, processed);
    }
}

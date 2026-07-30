package com.todo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "todo.reminder", name = "scheduler-enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {
    private final TodoReminderService todoReminderService;

    @Scheduled(
            initialDelayString = "${todo.reminder.initial-delay-ms:60000}",
            fixedDelayString = "${todo.reminder.scan-delay-ms:60000}"
    )
    public void scanDueReminders() {
        int processedCount = todoReminderService.processDueServerReminders();
        if (processedCount > 0) {
            log.info("Processed {} due server reminders.", processedCount);
        }
    }
}

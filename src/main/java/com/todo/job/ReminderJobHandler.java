package com.todo.job;

import com.todo.service.TodoReminderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderJobHandler {
    private final TodoReminderService todoReminderService;

    @XxlJob("scanDueReminderJob")
    public void scanDueReminderJob() {
        int publishedCount = todoReminderService.publishDueServerReminders();
        log.info("XXL-JOB published due reminder messages. publishedCount={}", publishedCount);
    }
}

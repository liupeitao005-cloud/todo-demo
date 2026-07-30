package com.todo.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class XxlJobDemoHandler {
    @XxlJob("todoDemoJob")
    public void todoDemoJob() {
        log.info("XXL-JOB todo demo job executed at {}", LocalDateTime.now());
    }
}

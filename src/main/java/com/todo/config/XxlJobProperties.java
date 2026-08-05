package com.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "todo.xxl-job")
public class XxlJobProperties {
    public static final String DEFAULT_ADMIN_ADDRESSES = "http://127.0.0.1:8088/xxl-job-admin";
    public static final String DEFAULT_ACCESS_TOKEN = "default_token";
    public static final String DEFAULT_EXECUTOR_APP_NAME = "todo-executor";
    public static final int DEFAULT_EXECUTOR_PORT = 9999;
    public static final String DEFAULT_EXECUTOR_LOG_PATH = "logs/xxl-job";
    public static final int DEFAULT_EXECUTOR_LOG_RETENTION_DAYS = 30;

    private String adminAddresses = DEFAULT_ADMIN_ADDRESSES;
    private String accessToken = DEFAULT_ACCESS_TOKEN;
    private Executor executor = new Executor();

    @Data
    public static class Executor {
        private String appName = DEFAULT_EXECUTOR_APP_NAME;
        private String address = "";
        private String ip = "";
        private int port = DEFAULT_EXECUTOR_PORT;
        private String logPath = DEFAULT_EXECUTOR_LOG_PATH;
        private int logRetentionDays = DEFAULT_EXECUTOR_LOG_RETENTION_DAYS;
    }
}

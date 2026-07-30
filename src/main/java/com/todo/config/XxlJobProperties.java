package com.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "todo.xxl-job")
public class XxlJobProperties {
    private String adminAddresses;
    private String accessToken;
    private Executor executor = new Executor();

    @Data
    public static class Executor {
        private String appName;
        private String address;
        private String ip;
        private int port;
        private String logPath;
        private int logRetentionDays;
    }
}

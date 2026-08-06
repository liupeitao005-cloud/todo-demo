package com.todo.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(XxlJobProperties.class)
@ConditionalOnProperty(prefix = "todo.xxl-job", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class XxlJobConfig {
    private final XxlJobProperties properties;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobProperties.Executor executorProperties = properties.getExecutor();
        String adminAddresses = textOrDefault(
                properties.getAdminAddresses(),
                XxlJobProperties.DEFAULT_ADMIN_ADDRESSES
        );
        String accessToken = textOrDefault(
                properties.getAccessToken(),
                null
        );
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalStateException("todo.xxl-job.access-token must be configured");
        }
        String appName = textOrDefault(
                executorProperties.getAppName(),
                XxlJobProperties.DEFAULT_EXECUTOR_APP_NAME
        );
        int port = executorProperties.getPort() > 0
                ? executorProperties.getPort()
                : XxlJobProperties.DEFAULT_EXECUTOR_PORT;
        String logPath = textOrDefault(
                executorProperties.getLogPath(),
                XxlJobProperties.DEFAULT_EXECUTOR_LOG_PATH
        );
        int logRetentionDays = executorProperties.getLogRetentionDays() > 0
                ? executorProperties.getLogRetentionDays()
                : XxlJobProperties.DEFAULT_EXECUTOR_LOG_RETENTION_DAYS;

        log.info("Initialize XXL-JOB executor. adminAddresses={}, appName={}, port={}",
                adminAddresses, appName, port);
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appName);
        executor.setAddress(executorProperties.getAddress());
        executor.setIp(executorProperties.getIp());
        executor.setPort(port);
        executor.setAccessToken(accessToken);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
        return executor;
    }

    private String textOrDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}

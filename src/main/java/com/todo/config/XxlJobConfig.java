package com.todo.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(XxlJobProperties.class)
@RequiredArgsConstructor
@Slf4j
public class XxlJobConfig {
    private final XxlJobProperties properties;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobProperties.Executor executorProperties = properties.getExecutor();
        log.info("Initialize XXL-JOB executor. adminAddresses={}, appName={}, port={}",
                properties.getAdminAddresses(), executorProperties.getAppName(), executorProperties.getPort());
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(properties.getAdminAddresses());
        executor.setAppname(executorProperties.getAppName());
        executor.setAddress(executorProperties.getAddress());
        executor.setIp(executorProperties.getIp());
        executor.setPort(executorProperties.getPort());
        executor.setAccessToken(properties.getAccessToken());
        executor.setLogPath(executorProperties.getLogPath());
        executor.setLogRetentionDays(executorProperties.getLogRetentionDays());
        return executor;
    }
}

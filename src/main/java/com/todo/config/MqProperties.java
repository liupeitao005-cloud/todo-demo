package com.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "todo.mq")
public class MqProperties {
    private Destination reminder = new Destination();

    @Data
    public static class Destination {
        private String exchange;
        private String queue;
        private String routingKey;
    }
}

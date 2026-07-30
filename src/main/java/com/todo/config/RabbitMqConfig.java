package com.todo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MqProperties.class)
public class RabbitMqConfig {
    private final MqProperties properties;

    @Bean
    public DirectExchange todoTestExchange() {

        return new DirectExchange(properties.getTest().getExchange(), true, false);
    }

    @Bean
    public Queue todoTestQueue() {

        return new Queue(properties.getTest().getQueue(), true);
    }

    @Bean
    public Binding todoTestBinding(DirectExchange todoTestExchange, Queue todoTestQueue) {
        return BindingBuilder.bind(todoTestQueue)
                .to(todoTestExchange)
                .with(properties.getTest().getRoutingKey());
    }

    @Bean
    public DirectExchange todoReminderExchange() {

        return new DirectExchange(properties.getReminder().getExchange(), true, false);
    }

    @Bean
    public Queue todoReminderQueue() {

        return new Queue(properties.getReminder().getQueue(), true);
    }

    @Bean
    public Binding todoReminderBinding(DirectExchange todoReminderExchange, Queue todoReminderQueue) {
        return BindingBuilder.bind(todoReminderQueue)
                .to(todoReminderExchange)
                .with(properties.getReminder().getRoutingKey());
    }
}

package com.todo.controller;

import com.todo.mq.MqTestProducer;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mq")
@RequiredArgsConstructor
public class MqTestController {
    private final MqTestProducer mqTestProducer;

    @GetMapping("/send")
    public Result<String> send(@RequestParam(required = false) String message) {
        String payload = StringUtils.hasText(message) ? message : "hello rabbitmq";
        mqTestProducer.send(payload);
        return Result.success("MQ消息发送成功", payload);
    }
}

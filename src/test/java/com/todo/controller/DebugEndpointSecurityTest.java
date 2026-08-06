package com.todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DebugEndpointSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void redisDebugEndpointCannotBeCalledAnonymously() throws Exception {
        mockMvc.perform(get("/redis/ping"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mqDebugEndpointCannotBeCalledAnonymously() throws Exception {
        mockMvc.perform(get("/mq/send"))
                .andExpect(status().isUnauthorized());
    }
}

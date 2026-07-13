package com.todo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI todoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo 待办与日程管理系统接口文档")
                        .version("1.0")
                        .description("用于展示用户、待办、任务、日程、习惯、四象限、复习计划和提醒等接口"))
                .components(new Components()
                        .addSecuritySchemes("tokenAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("token")
                                .description("登录接口返回的 JWT")));
    }
}

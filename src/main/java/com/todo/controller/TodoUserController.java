package com.todo.controller;

import com.todo.dto.TodoUserDTO;
import com.todo.service.TodoUserService;
import com.todo.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户接口", description = "用户注册和登录")
public class TodoUserController {
    private final TodoUserService todoUserService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody TodoUserDTO request) {
        return todoUserService.register(request);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody TodoUserDTO request) {
        return todoUserService.login(request);
    }
}

package com.todo.controller;

import com.todo.dto.TodoUserDTO;
import com.todo.service.TodoUserService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class TodoUserController {
    private final TodoUserService todoUserService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody TodoUserDTO request) {
        return todoUserService.register(request);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody TodoUserDTO request) {
        return todoUserService.login(request);
    }
}

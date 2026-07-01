package com.todo.controller;

import com.todo.dto.UserRequest;
import com.todo.service.UserService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRequest request) {

        return userService.register(request);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserRequest request) {

        return userService.login(request);
    }
}

package com.todo.service;

import com.todo.dto.TodoUserDTO;
import com.todo.entity.TodoUser;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.mapper.TodoUserMapper;
import com.todo.util.JwtUtil;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TodoUserService {
    private final TodoUserMapper todoUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TodoScheduleMapper todoScheduleMapper;

    public Result<String> register(TodoUserDTO request) {
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            return Result.fail("用户名和密码不能为空");
        }
        String username = request.getUsername().trim();
        if (todoUserMapper.selectByUsername(username) != null) {
            return Result.fail("账号已存在");
        }
        TodoUser user = new TodoUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        todoUserMapper.insert(user);
        return Result.success("注册成功");
    }

    public Result<String> login(TodoUserDTO request) {
        if (request == null || !StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            return Result.fail("账号或密码错误");
        }
        TodoUser user = todoUserMapper.selectByUsername(request.getUsername().trim());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.fail("账号或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId());
        return Result.success("登录成功", token);
    }
}

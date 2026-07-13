package com.todo.service;

import com.todo.dto.TodoUserDTO;
import com.todo.entity.TodoUser;
import com.todo.mapper.TodoScheduleMapper;
import com.todo.mapper.TodoUserMapper;
import com.todo.util.JwtUtil;
import com.todo.util.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoUserServiceTest {

    @Mock
    private TodoUserMapper todoUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TodoScheduleMapper todoScheduleMapper;

    @InjectMocks
    private TodoUserService todoUserService;

    @Test
    void registerSuccess() {
        TodoUserDTO dto = new TodoUserDTO();
        dto.setUsername("demo");
        dto.setPassword("123456");
        when(todoUserMapper.selectByUsername("demo")).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        Result<String> result = todoUserService.register(dto);
        assertEquals(200, result.getCode());
        assertEquals("注册成功", result.getMessage());
        ArgumentCaptor<TodoUser> userCaptor = ArgumentCaptor.forClass(TodoUser.class);
        verify(todoUserMapper).insert(userCaptor.capture());
        assertEquals("demo", userCaptor.getValue().getUsername());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
    }

    @Test
    void registerFailWhenUsernameExists() {
        TodoUserDTO dto = new TodoUserDTO();
        dto.setUsername("demo");
        dto.setPassword("123456");
        when(todoUserMapper.selectByUsername("demo")).thenReturn(new TodoUser());
        Result<String> result = todoUserService.register(dto);
        assertEquals(400, result.getCode());
        assertEquals("账号已存在", result.getMessage());
        verify(todoUserMapper, never()).insert(any(TodoUser.class));
    }

    @Test
    void loginSuccess() {
        TodoUserDTO dto = new TodoUserDTO();
        dto.setUsername("demo");
        dto.setPassword("123456");
        TodoUser user = new TodoUser();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("encoded-password");
        when(todoUserMapper.selectByUsername("demo")).thenReturn(user);
        when(passwordEncoder.matches("123456", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken(1L)).thenReturn("mock-token");
        Result<String> result = todoUserService.login(dto);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMessage());
        assertEquals("mock-token", result.getData());
    }

    @Test
    void loginFailWhenUserNotFound() {
        TodoUserDTO dto = new TodoUserDTO();
        dto.setUsername("demo");
        dto.setPassword("123456");
        when(todoUserMapper.selectByUsername("demo")).thenReturn(null);
        Result<String> result = todoUserService.login(dto);
        assertEquals(400, result.getCode());
        assertEquals("账号或密码错误", result.getMessage());
        assertNull(result.getData());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void loginFailWhenPasswordWrong() {
        TodoUserDTO dto = new TodoUserDTO();
        dto.setUsername("demo");
        dto.setPassword("wrong-password");
        TodoUser user = new TodoUser();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("encoded-password");
        when(todoUserMapper.selectByUsername("demo")).thenReturn(user);
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);
        Result<String> result = todoUserService.login(dto);
        assertEquals(400, result.getCode());
        assertEquals("账号或密码错误", result.getMessage());
        assertNull(result.getData());
        verify(jwtUtil, never()).generateToken(any());
    }
}

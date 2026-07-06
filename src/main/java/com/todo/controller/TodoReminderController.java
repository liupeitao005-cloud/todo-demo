package com.todo.controller;

import com.todo.dto.TodoReminderDTO;
import com.todo.service.TodoReminderService;
import com.todo.util.Result;
import com.todo.vo.TodoReminderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
public class TodoReminderController {
    private final TodoReminderService todoReminderService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody TodoReminderDTO dto) {
        return todoReminderService.createReminder(dto);
    }

    @GetMapping("/pending")
    public Result<List<TodoReminderVO>> pending() {
        return todoReminderService.pendingDesktopReminder();
    }

    @GetMapping("/app/pending")
    public Result<List<TodoReminderVO>> appPending() {
        return todoReminderService.pendingAppReminder();
    }
    @GetMapping("/telegramBot/pending")
    public Result<List<TodoReminderVO>> telegramBotPending() {
        return todoReminderService.pendingTelegramBotReminder();
    }

    @PutMapping("/read")
    public Result<String> read(@RequestBody TodoReminderDTO dto) {
        return todoReminderService.readReminder(dto);
    }
}

package com.todo.controller;

import com.todo.dto.TodoReminderDTO;
import com.todo.entity.TodoReminder;
import com.todo.service.TodoReminderService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/pending")
    public Result<List<TodoReminder>> pending() {
        return todoReminderService.pendingDesktopReminder();
    }

    @PostMapping("/app/pending")
    public Result<List<TodoReminder>> appPending() {
        return todoReminderService.pendingAppReminder();
    }

    @PostMapping("/read")
    public Result<String> read(@RequestBody TodoReminderDTO dto) {
        return todoReminderService.readReminder(dto);
    }
}

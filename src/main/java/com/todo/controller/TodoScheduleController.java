package com.todo.controller;

import com.todo.dto.TodoScheduleDTO;
import com.todo.service.TodoScheduleService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class TodoScheduleController {
    private final TodoScheduleService todoScheduleService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.createtodoSchedule(dto);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.updateTodoSchedule(dto);
    }

    @PostMapping("/delete")
    public Result<String> delete(@RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.deleteTodoSchedule(dto);
    }
}

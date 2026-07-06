package com.todo.controller;

import com.todo.dto.TodoScheduleDTO;
import com.todo.service.TodoScheduleService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class TodoScheduleController {
    private final TodoScheduleService todoScheduleService;

    @PostMapping("/create")
    public Result<String> create(@RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.createtodoSchedule(dto);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.updateTodoSchedule(dto);
    }

    @DeleteMapping("/delete")
    public Result<String> delete(@RequestBody TodoScheduleDTO dto) {
        return todoScheduleService.deleteTodoSchedule(dto);
    }
}

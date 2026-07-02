package com.todo.controller;

import com.todo.dto.TodoAiScheduleDTO;
import com.todo.dto.TodoScheduleDTO;
import com.todo.service.TodoAiScheduleService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/schedule")
@RequiredArgsConstructor
public class TodoAiScheduleController {
    private final TodoAiScheduleService todoAiScheduleService;

    @PostMapping("/doubao")
    public Result<TodoScheduleDTO> draft(@RequestBody TodoAiScheduleDTO dto) {
        return todoAiScheduleService.draftSchedule(dto);
    }
}

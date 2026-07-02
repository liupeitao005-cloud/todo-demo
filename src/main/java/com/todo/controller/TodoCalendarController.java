package com.todo.controller;

import com.todo.dto.TodoCalendarDTO;
import com.todo.entity.TodoCalendarItem;
import com.todo.service.TodoCalendarService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class TodoCalendarController {
    private final TodoCalendarService todoCalendarService;

    @PostMapping("/select")
    public Result<List<TodoCalendarItem>> select(@RequestBody TodoCalendarDTO dto) {
        return todoCalendarService.listCalendar(dto);
    }
}

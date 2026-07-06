package com.todo.controller;

import com.todo.dto.TodoCalendarDTO;
import com.todo.service.TodoCalendarService;
import com.todo.util.Result;
import com.todo.vo.TodoCalendarVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class TodoCalendarController {
    private final TodoCalendarService todoCalendarService;

    @GetMapping("/select")
    public Result<List<TodoCalendarVO>> select(TodoCalendarDTO dto) {

        return todoCalendarService.listCalendar(dto);
    }
}

package com.todo.controller;


import com.todo.dto.TodoHabbitDTO;
import com.todo.service.TodoHabbitService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/habbit")
public class TodoHabbitController {
    private final TodoHabbitService todoHabbitService;

    @PostMapping("/create")
    public Result<String> create (@RequestBody TodoHabbitDTO dto){
        return todoHabbitService.createHabbit(dto);
    }
}

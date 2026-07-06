package com.todo.controller;

import com.todo.dto.TodoBacklogDTO;
import com.todo.util.Result;
import com.todo.service.TodoBacklogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("backlog")
@RequiredArgsConstructor
public class TodoBacklogController {
    private final TodoBacklogService todoBacklogService;
    @PostMapping("/create")
    public Result<String> create(@RequestBody TodoBacklogDTO dto){
        return todoBacklogService.createtodoBacklog(dto);
    }
    @PutMapping("/update")
    public Result<String> update(@RequestBody TodoBacklogDTO dto){
        return todoBacklogService.updateBacklog(dto);
    }
    @PostMapping("/move")
    public Result<String> move(@RequestBody TodoBacklogDTO dto) {
        return todoBacklogService.moveTodoBacklog(dto);
    }
}

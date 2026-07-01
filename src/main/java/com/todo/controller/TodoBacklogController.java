package com.todo.controller;

import com.todo.dto.TodoBacklogDTO;
import com.todo.util.Result;
import org.springframework.web.bind.annotation.PostMapping;
import com.todo.service.TodoBacklogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("backlog")
@RequiredArgsConstructor
public class TodoBacklogController {
    private final TodoBacklogService todoBacklogService;
    @PostMapping("/create")
    public Result<String> create(@RequestBody TodoBacklogDTO dto){
        return todoBacklogService.createtodoBacklog(dto);
    }

    @PostMapping("/update")
    public Result<String> update(@RequestBody TodoBacklogDTO dto){
        return todoBacklogService.updateBacklog(dto);
    }
}

package com.todo.controller;

import com.todo.dto.TodoBacklogDTO;
import com.todo.entity.TodoBacklog;
import com.todo.validation.ValidationGroups;
import com.todo.util.Result;
import com.todo.service.TodoBacklogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("backlog")
@RequiredArgsConstructor
@Tag(name = "待办箱接口", description = "待办创建、修改、安排和查询")
@SecurityRequirement(name = "tokenAuth")
public class TodoBacklogController {
    private final TodoBacklogService todoBacklogService;

    @Operation(summary = "创建待办")
    @PostMapping("/create")
    public Result<String> create(@Validated(ValidationGroups.Create.class) @RequestBody TodoBacklogDTO dto){
        return todoBacklogService.createtodoBacklog(dto);
    }

    @Operation(summary = "修改待办")
    @PutMapping("/update")
    public Result<String> update(@Validated(ValidationGroups.Update.class) @RequestBody TodoBacklogDTO dto){
        return todoBacklogService.updateBacklog(dto);
    }

    @Operation(summary = "安排待办为任务")
    @PostMapping("/move")
    public Result<String> move(@Validated(ValidationGroups.Move.class) @RequestBody TodoBacklogDTO dto) {
        return todoBacklogService.moveTodoBacklog(dto);
    }

    @Operation(summary = "删除待办")
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE, RequestMethod.POST})
    public Result<String> delete(@RequestParam(required = false) Long id,
                                 @RequestBody(required = false) TodoBacklogDTO dto) {
        Long backlogId = id != null ? id : (dto == null ? null : dto.getId());
        return todoBacklogService.deleteBacklog(backlogId);
    }

    @Operation(summary = "查询待办列表")
    @GetMapping("/select")
    public  Result<List<TodoBacklog>> list(){
        return todoBacklogService.listBacklog();
    }
}

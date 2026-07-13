package com.todo.controller;


import com.todo.dto.TodoReviewDTO;
import com.todo.entity.TodoReviewplan;
import com.todo.service.TodoReviewService;
import com.todo.util.Result;
import com.todo.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name = "复习计划接口", description = "复习任务创建、复习完成和复习计划查询")
@SecurityRequirement(name = "tokenAuth")
public class TodoReviewController {
    private final TodoReviewService todoReviewService;

    @Operation(summary = "创建复习任务并生成复习计划")
    @PostMapping("/create")
    public Result<String> create (@Validated(ValidationGroups.Create.class) @RequestBody TodoReviewDTO dto){
        return todoReviewService.createReview(dto);
    }

    @Operation(summary = "完成复习计划")
    @PutMapping("/finish")
    public Result<String> finish(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoReviewDTO dto) {
        return todoReviewService.finishReviewPlan(dto.getId());
    }

    @Operation(summary = "查询复习计划列表")
    @GetMapping("/plans")
    public Result<List<TodoReviewplan>> list() {
        return todoReviewService.listReviewPlan();
    }
}

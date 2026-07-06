package com.todo.controller;


import com.todo.dto.TodoReviewDTO;
import com.todo.service.TodoReviewService;
import com.todo.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class TodoReviewController {
    private final TodoReviewService todoReviewService;
    @PostMapping("/create")
    public Result<String> create (@RequestBody TodoReviewDTO dto){
        return todoReviewService.createReview(dto);
    }
    @PutMapping("/finish")
    public Result<String> finish(@RequestBody TodoReviewDTO dto) {
        return todoReviewService.finishReviewPlan(dto);
    }
}

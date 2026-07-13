package com.todo.controller;

import com.todo.dto.TodoCalendarDTO;
import com.todo.service.TodoCalendarService;
import com.todo.util.Result;
import com.todo.validation.ValidationGroups;
import com.todo.vo.TodoCalendarVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
@Tag(name = "日历接口", description = "按时间范围聚合查询任务和行程")
@SecurityRequirement(name = "tokenAuth")
public class TodoCalendarController {
    private final TodoCalendarService todoCalendarService;

    @Operation(summary = "查询日历数据")
    @GetMapping("/select")
    public Result<List<TodoCalendarVO>> select(@ParameterObject @Validated(ValidationGroups.Query.class) TodoCalendarDTO dto) {

        return todoCalendarService.listCalendar(dto);
    }
}

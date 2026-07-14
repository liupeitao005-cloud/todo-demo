package com.todo.controller;

import com.todo.dto.TodoReminderDTO;
import com.todo.entity.TodoReminder;
import com.todo.service.TodoReminderService;
import com.todo.util.Result;
import com.todo.validation.ValidationGroups;
import com.todo.vo.TodoReminderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reminder")
@RequiredArgsConstructor
@Tag(name = "提醒接口", description = "提醒创建、列表查询、到期提醒查询和已读标记")
@SecurityRequirement(name = "tokenAuth")
public class TodoReminderController {
    private final TodoReminderService todoReminderService;

    @Operation(summary = "创建提醒")
    @PostMapping("/create")
    public Result<String> create(@Validated(ValidationGroups.Create.class) @RequestBody TodoReminderDTO dto) {
        return todoReminderService.createReminder(dto);
    }

    @Operation(summary = "查询提醒列表")
    @GetMapping("/select")
    public Result<List<TodoReminder>> list() {
        return todoReminderService.listReminder();
    }

    @Operation(summary = "查询桌面端到期提醒")
    @GetMapping("/pending")
    public Result<List<TodoReminderVO>> pending() {
        return todoReminderService.pendingDesktopReminder();
    }

    @Operation(summary = "查询 App 到期提醒")
    @GetMapping("/app/pending")
    public Result<List<TodoReminderVO>> appPending() {
        return todoReminderService.pendingAppReminder();
    }

    @Operation(summary = "查询 Telegram 到期提醒")
    @GetMapping("/telegramBot/pending")
    public Result<List<TodoReminderVO>> telegramBotPending() {
        return todoReminderService.pendingTelegramBotReminder();
    }

    @Operation(summary = "标记提醒已读")
    @PutMapping("/read")
    public Result<String> read(@Validated(ValidationGroups.IdRequired.class) @RequestBody TodoReminderDTO dto) {
        return todoReminderService.readReminder(dto);
    }
}

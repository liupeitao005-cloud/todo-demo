package com.todo.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoReviewplan {
    private Long id;
    private Long userId;
    private Long reviewTaskId;
    private LocalDateTime reviewTime;
    private Integer isFinish;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

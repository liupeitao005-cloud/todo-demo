package com.todo.mapper;

import com.todo.entity.TodoReview;
import com.todo.entity.TodoReviewplan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodoReviewplanMapperTest extends MapperTestBase {

    @Autowired
    private TodoReviewMapper todoReviewMapper;
    @Autowired
    private TodoReviewplanMapper todoReviewplanMapper;

    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoReviewplan reviewplan = newReviewplan(userId);
        int row = todoReviewplanMapper.insert(reviewplan);
        assertEquals(1, row);
        assertNotNull(reviewplan.getId());
    }

    @Test
    void finishSuccess() {
        Long userId = createUser();
        TodoReviewplan reviewplan = newReviewplan(userId);
        todoReviewplanMapper.insert(reviewplan);
        int row = todoReviewplanMapper.finish(reviewplan.getId(), userId);
        assertEquals(1, row);
    }

    private TodoReviewplan newReviewplan(Long userId) {
        TodoReview review = newReview(userId);
        todoReviewMapper.insert(review);
        TodoReviewplan reviewplan = new TodoReviewplan();
        reviewplan.setUserId(userId);
        reviewplan.setReviewTaskId(review.getId());
        reviewplan.setReviewTime(LocalDateTime.now().plusDays(1));
        reviewplan.setIsFinish(0);
        return reviewplan;
    }

    private TodoReview newReview(Long userId) {
        TodoReview review = new TodoReview();
        review.setUserId(userId);
        review.setTitle("test review");
        review.setContent("test content");
        return review;
    }
}

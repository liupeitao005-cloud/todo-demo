package com.todo.mapper;

import com.todo.entity.TodoReview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodoReviewMapperTest extends MapperTestBase {

    @Autowired
    private TodoReviewMapper todoReviewMapper;

    @Test
    void insertSuccess() {
        Long userId = createUser();
        TodoReview review = newReview(userId);
        int row = todoReviewMapper.insert(review);
        assertEquals(1, row);
        assertNotNull(review.getId());
    }

    private TodoReview newReview(Long userId) {
        TodoReview review = new TodoReview();
        review.setUserId(userId);
        review.setTitle("test review");
        review.setContent("test content");
        return review;
    }
}

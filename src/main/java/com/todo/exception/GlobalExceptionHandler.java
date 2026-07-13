package com.todo.exception;

import com.todo.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return Result.fail(getFieldErrorMessage(ex.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException ex) {
        return Result.fail(getFieldErrorMessage(ex.getBindingResult().getFieldError()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return Result.fail("请求参数格式错误");
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception ex) {
        log.error("系统异常", ex);
        return Result.fail("系统异常，请稍后再试");
    }

    private String getFieldErrorMessage(FieldError fieldError) {
        if (fieldError == null) {
            return "请求参数错误";
        }
        return fieldError.getDefaultMessage();
    }
}

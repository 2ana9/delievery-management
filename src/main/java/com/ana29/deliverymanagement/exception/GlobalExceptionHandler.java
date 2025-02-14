package com.ana29.deliverymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCustomNotFoundException(CustomNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AlreadyReviewedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAlreadyReviewedException(AlreadyReviewedException e) {
        return e.getMessage();
    }
}

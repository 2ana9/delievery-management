package com.ana29.deliverymanagement.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private String errorMessage;
    private int statusCode;
}
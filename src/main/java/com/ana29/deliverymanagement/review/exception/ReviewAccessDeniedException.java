package com.ana29.deliverymanagement.review.exception;

import com.ana29.deliverymanagement.global.exception.CustomAccessDeniedException;

import java.util.UUID;

public class ReviewAccessDeniedException extends CustomAccessDeniedException {
    public ReviewAccessDeniedException(UUID reviewId) {
        super("접근 권한이 없습니다. ID: " + reviewId);
    }
}

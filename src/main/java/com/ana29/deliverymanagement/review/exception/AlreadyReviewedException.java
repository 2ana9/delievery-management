package com.ana29.deliverymanagement.review.exception;

public class AlreadyReviewedException extends RuntimeException {
    public AlreadyReviewedException(String message) {
        super(message);
    }
}

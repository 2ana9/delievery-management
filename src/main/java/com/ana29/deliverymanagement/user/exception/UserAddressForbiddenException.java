package com.ana29.deliverymanagement.user.exception;

public class UserAddressForbiddenException extends RuntimeException {

    public UserAddressForbiddenException() {
        super("유저 검증에 실패하였습니다.");
    }
}

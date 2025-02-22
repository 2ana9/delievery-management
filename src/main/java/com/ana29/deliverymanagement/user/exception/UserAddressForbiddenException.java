package com.ana29.deliverymanagement.user.exception;

import com.ana29.deliverymanagement.global.exception.CustomForbiddenException;

public class UserAddressForbiddenException extends CustomForbiddenException {

    public UserAddressForbiddenException() {
        super("유저 검증에 실패하였습니다.");
    }
}

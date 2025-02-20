package com.ana29.deliverymanagement.user.exception;

import com.ana29.deliverymanagement.global.exception.CustomNotFoundException;

public class UserAddressNotFoundException extends CustomNotFoundException {

    public UserAddressNotFoundException() {
        super("주소정보를 찾을수 없습니다.");
    }
}
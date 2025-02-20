package com.ana29.deliverymanagement.user.exception;

import com.ana29.deliverymanagement.global.exception.CustomConflictException;

public class DuplicateAddressException extends CustomConflictException {

    public DuplicateAddressException(String address) {
        super("이미 등록된 배송지입니다. 주소: " + address);
    }
}
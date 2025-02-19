package com.ana29.deliverymanagement.user.exception;

public class DuplicateAddressException extends IllegalArgumentException {

    public DuplicateAddressException(String address) {
        super("이미 등록된 배송지입니다. 주소: " + address);
    }
}
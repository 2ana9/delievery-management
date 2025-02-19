package com.ana29.deliverymanagement.user.exception;

public class UserAddressNotFoundException extends RuntimeException {

    public UserAddressNotFoundException() {
        super("주소정보를 찾을수 없습니다.");
    }
}
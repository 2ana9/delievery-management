package com.ana29.deliverymanagement.user.exception;

public class UserAddressNotModifiedException extends RuntimeException {

    public UserAddressNotModifiedException() {
        super("변경할 주소 정보가 없습니다.");
    }
}
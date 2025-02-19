package com.ana29.deliverymanagement.user.exception;

public class AddressLimitExceededException extends RuntimeException {
    public AddressLimitExceededException() {
        super("최대 10개의 배송지만 등록할 수 있습니다.");
    }
}

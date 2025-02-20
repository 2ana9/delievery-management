package com.ana29.deliverymanagement.user.dto;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.user.entity.UserAddress;

import java.util.UUID;

public record CreateUserAddressResponseDto(
		UUID userAddressId,
		String address,
		String detail,
		Boolean defaultAddress
) {
	public static CreateUserAddressResponseDto from(UserAddress userAddress) {
		return new CreateUserAddressResponseDto(
				userAddress.getId(),
				userAddress.getAddress(),
				userAddress.getDetail(),
				userAddress.getDefaultAddress()
		);
	}
}
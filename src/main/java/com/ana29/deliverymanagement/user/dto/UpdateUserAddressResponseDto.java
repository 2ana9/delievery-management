package com.ana29.deliverymanagement.user.dto;

import com.ana29.deliverymanagement.user.entity.UserAddress;

import java.util.UUID;

public record UpdateUserAddressResponseDto(
		UUID userAddressId,
		String address,
		String detail,
		Boolean defaultAddress
) {
	public static UpdateUserAddressResponseDto from(UserAddress userAddress) {
		return new UpdateUserAddressResponseDto(
				userAddress.getId(),
				userAddress.getAddress(),
				userAddress.getDetail(),
				userAddress.getDefaultAddress()
		);
	}
}
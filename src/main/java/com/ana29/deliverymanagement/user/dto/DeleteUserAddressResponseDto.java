package com.ana29.deliverymanagement.user.dto;

import com.ana29.deliverymanagement.user.entity.UserAddress;

import java.util.UUID;

public record DeleteUserAddressResponseDto(
		UUID userAddressId
) {
	public static DeleteUserAddressResponseDto from(UserAddress userAddress) {
		return new DeleteUserAddressResponseDto(
				userAddress.getId()
		);
	}
}
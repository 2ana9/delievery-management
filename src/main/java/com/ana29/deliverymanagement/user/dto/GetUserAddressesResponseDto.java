package com.ana29.deliverymanagement.user.dto;

import com.ana29.deliverymanagement.user.entity.UserAddress;
import java.util.UUID;
import lombok.Builder;

@Builder
public record GetUserAddressesResponseDto(UUID userAddressId,
										  String address,
										  String detail,
										  boolean defaultAddress
) {

	public static GetUserAddressesResponseDto from(UserAddress userAddress) {
		return userAddress != null ?
			new GetUserAddressesResponseDto
				(userAddress.getId(), userAddress.getAddress(), userAddress.getDetail(), userAddress.getDefaultAddress())
			: null;
	}
}
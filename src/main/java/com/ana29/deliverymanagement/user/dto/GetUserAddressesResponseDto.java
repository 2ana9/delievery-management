package com.ana29.deliverymanagement.user.dto;

import java.util.UUID;

public record GetUserAddressesResponseDto(UUID userAddressId,
										  String address,
										  String detail,
										  Boolean defaultAddress
										  ) {}
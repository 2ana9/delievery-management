package com.ana29.deliverymanagement.user.dto;

public record GetUserAddressesRequestDto(Boolean isAsc) {
	public GetUserAddressesRequestDto {
		isAsc = isAsc != null ? isAsc : false;
	}
}
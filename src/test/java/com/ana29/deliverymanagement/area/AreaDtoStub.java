package com.ana29.deliverymanagement.area;

import com.ana29.deliverymanagement.user.dto.CreateUserAddressRequestDto;
import com.ana29.deliverymanagement.user.dto.CreateUserAddressResponseDto;
import java.util.UUID;

public class AreaDtoStub {

	public static final UUID TEST_ORDER_ID = UUID.fromString(
		"550e8400-e29b-41d4-a716-446655440000");
	public static final UUID TEST_MENU_ID = UUID.fromString("550e8400-e29b-41d4-a716"
		+ "-446655440001");
	public static final UUID TEST_RESTAURANT_ID = UUID.fromString(
		"550e8400-e29b-41d4-a716-446655440002");
	public static final UUID TEST_USER_ADDRESS_ID = UUID.fromString(
		"550e8400-e29b-41d4-a716-446655440003");
	public static final UUID TEST_EXTERNAL_PAYMENT_ID = UUID.fromString(
		"550e8400-e29b-41d4-a716-446655440004");

	public static CreateUserAddressRequestDto createUserAddressRequestDto() {
		return new CreateUserAddressRequestDto(
				"대구 서구 북비산로 생성34",
				"522-13번지 3층"
		);
	}

	public static CreateUserAddressResponseDto createUserAddressResponseDto() {
		return new CreateUserAddressResponseDto(
				TEST_USER_ADDRESS_ID,
				"대구 서구 북비산로 생성34",
				"522-13번지 3층",
				false
		);
	}
}

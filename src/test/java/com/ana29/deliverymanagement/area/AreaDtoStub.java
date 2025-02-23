package com.ana29.deliverymanagement.area;

import com.ana29.deliverymanagement.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public class AreaDtoStub {

	public static final UUID TEST_USER_ADDRESS_ID = UUID.fromString(
		"550e8400-e29b-41d4-a716-446655440003");

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

	public static UpdateUserAddressRequestDto updateUserAddressRequestDto() {
		return new UpdateUserAddressRequestDto(
				"대구 서구 북비산로 생성34",
				"522-13번지 3층"
		);
	}

	public static UpdateUserAddressResponseDto updateUserAddressResponseDto() {
		return new UpdateUserAddressResponseDto(
				TEST_USER_ADDRESS_ID,
				"대구 서구 북비산로 생성34",
				"522-13번지 3층",
				false
		);
	}


	public static GetUserAddressesResponseDto getUserAddressesResponseDtoStub() {
		return GetUserAddressesResponseDto.builder()
				.userAddressId(TEST_USER_ADDRESS_ID)
				.address("서울시 강남구 테헤란로 123")
				.detail("101동 1001호")
				.defaultAddress(false)
				.build();
	}

	public static DeleteUserAddressResponseDto deleteUserAddressResponseDto() {
		return new DeleteUserAddressResponseDto(
				TEST_USER_ADDRESS_ID
		);
	}

	public static Page<GetUserAddressesResponseDto> getUserAddressesResponsePage() {
		List<GetUserAddressesResponseDto> orders = List.of(getUserAddressesResponseDtoStub());
		return new PageImpl<>(orders, PageRequest.of(0, 10), 1);
	}
}

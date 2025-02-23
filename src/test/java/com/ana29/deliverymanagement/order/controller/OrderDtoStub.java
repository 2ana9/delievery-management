package com.ana29.deliverymanagement.order.controller;

import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
import com.ana29.deliverymanagement.global.constant.OrderTypeEnum;
import com.ana29.deliverymanagement.global.constant.PaymentStatusEnum;
import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.user.dto.GetUserAddressesResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class OrderDtoStub {

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

	public static CreateOrderRequestDto getOrderDetailsResponseDtoStub() {
		return new CreateOrderRequestDto(
			TEST_MENU_ID,
			2,
			"양념 많이 주세요",
			PaymentTypeEnum.CREDIT_CARD,
			OrderTypeEnum.ONLINE,
			TEST_USER_ADDRESS_ID
		);
	}

	public static OrderDetailResponseDto getOrderDetailsResponseDtoStub(String userId) {
		GetUserAddressesResponseDto addressInfo = new GetUserAddressesResponseDto(
			TEST_USER_ADDRESS_ID,
			"서울시 강남구 테헤란로 123",
			"101동 1001호",
				false
		);

		return OrderDetailResponseDto.builder()
			.orderId(TEST_ORDER_ID)
			.orderStatus(OrderStatusEnum.PENDING)
			.quantity(2)
			.orderRequest("양념 많이 주세요")
			.orderType(OrderTypeEnum.ONLINE)
			.addressInfo(addressInfo)
			.orderedAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.restaurantId(TEST_RESTAURANT_ID)
			.restaurantName("맛있는 치킨")
			.menuName("후라이드 치킨")
			.menuPrice(18000L)
			.totalPrice(36000L)
			.paymentStatus(PaymentStatusEnum.COMPLETED)
			.paymentType(PaymentTypeEnum.CREDIT_CARD)
			.externalPaymentId(TEST_EXTERNAL_PAYMENT_ID)
			.paidAt(LocalDateTime.now())
			.refundedAt(null)
			.createdBy(userId)
			.build();
	}

	public static OrderHistoryResponseDto createOrderHistoryResponseDto() {
		return new OrderHistoryResponseDto(
			TEST_ORDER_ID,
			TEST_RESTAURANT_ID,
			"맛있는 치킨",
			"한식",
			"후라이드 치킨",
			OrderStatusEnum.PENDING,
			OrderTypeEnum.ONLINE,
			LocalDateTime.now(),
			LocalDateTime.now()
		);
	}

	public static OrderDetailResponseDto getCanceledOrderResponseDtoStub(String userId) {
		OrderDetailResponseDto baseDto = getOrderDetailsResponseDtoStub(userId);
		return OrderDetailResponseDto.builder()
			.orderId(baseDto.orderId())
			.orderStatus(OrderStatusEnum.CANCELED)
			.quantity(baseDto.quantity())
			.orderRequest(baseDto.orderRequest())
			.orderType(baseDto.orderType())
			.addressInfo(baseDto.addressInfo())
			.orderedAt(baseDto.orderedAt())
			.updatedAt(baseDto.updatedAt())
			.restaurantId(baseDto.restaurantId())
			.restaurantName(baseDto.restaurantName())
			.menuName(baseDto.menuName())
			.menuPrice(baseDto.menuPrice())
			.totalPrice(baseDto.totalPrice())
			.paymentStatus(PaymentStatusEnum.REFUNDED)
			.paymentType(baseDto.paymentType())
			.externalPaymentId(baseDto.externalPaymentId())
			.paidAt(baseDto.paidAt())
			.refundedAt(LocalDateTime.now())
			.createdBy(baseDto.createdBy())
			.build();
	}

	public static Page<OrderHistoryResponseDto> createOrderHistoryPage() {
		List<OrderHistoryResponseDto> orders = List.of(createOrderHistoryResponseDto());
		return new PageImpl<>(orders, PageRequest.of(0, 10), 1);
	}

	public static OrderSearchCondition createOrderSearchCondition() {
		return new OrderSearchCondition(
			"치킨",
			List.of(OrderStatusEnum.PAID),
			LocalDate.now().minusDays(7),
			LocalDate.now(),
			false,
			null
		);
	}

	public static MockHttpServletRequestBuilder applyDefaultSearchParams(
		MockHttpServletRequestBuilder request) {
		return request
			.param("keyword", "치킨")
			.param("statuses", "PAID")
			.param("startDate", "2025-02-14")
			.param("endDate", "2025-02-21")
			.param("isAsc", "false")
			.param("page", "0")
			.param("size", "10");
	}

}

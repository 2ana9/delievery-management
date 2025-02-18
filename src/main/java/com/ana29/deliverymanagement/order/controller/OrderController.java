package com.ana29.deliverymanagement.order.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.order.service.OrderService;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<ResponseDto<OrderDetailResponseDto>> createOrder(
		@RequestBody @Valid CreateOrderRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		OrderDetailResponseDto response =
			orderService.createOrder(requestDto, userDetails.getUsername());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ResponseDto<>(HttpStatus.CREATED, response));
	}

	@GetMapping("/history")
	public ResponseEntity<ResponseDto<Page<OrderHistoryResponseDto>>> getOrderHistory(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@ModelAttribute OrderSearchCondition condition, Pageable pageable) {

		Page<OrderHistoryResponseDto> response =
			orderService.getOrderHistory(condition, pageable, userDetails.getUsername());
		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseDto<>(HttpStatus.OK, response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseDto<OrderDetailResponseDto>> getOrderDetail(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("id") UUID id) {

		OrderDetailResponseDto response =
			orderService.getOrderDetail(id, userDetails.getUsername());

		return ResponseEntity.status(HttpStatus.OK)
			.body(new ResponseDto<>(HttpStatus.OK, response));
	}
}

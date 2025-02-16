package com.ana29.deliverymanagement.controller;

import com.ana29.deliverymanagement.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.dto.CreateOrderResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
	public ResponseEntity<CreateOrderResponseDto> createOrder(
		@RequestBody @Valid CreateOrderRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		CreateOrderResponseDto responseDto =
			orderService.createOrder(requestDto, userDetails.getUsername());
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}
}

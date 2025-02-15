package com.ana29.deliverymanagement.controller;

import com.ana29.deliverymanagement.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequestDto requestDto,
		@RequestParam String userId //@AuthenticationPrincipal 로 변경할 것, 수정필요
	) {
		orderService.createOrder(requestDto, userId);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}

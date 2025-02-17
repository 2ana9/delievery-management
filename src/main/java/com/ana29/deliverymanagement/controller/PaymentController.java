package com.ana29.deliverymanagement.controller;

import com.ana29.deliverymanagement.dto.CreatePaymentRequestDto;
import com.ana29.deliverymanagement.dto.PaymentResultDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ResponseEntity<PaymentResultDto> createPayment(@RequestBody @Valid CreatePaymentRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		PaymentResultDto resultDto = paymentService.createPayment(requestDto, userDetails.getUsername());
		return new ResponseEntity<>(resultDto, HttpStatus.CREATED);
	}
}

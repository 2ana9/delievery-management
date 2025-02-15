package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.dto.CreateOrderResponseDto;
import com.ana29.deliverymanagement.entity.Menu;
import com.ana29.deliverymanagement.entity.Order;
import com.ana29.deliverymanagement.entity.User;
import com.ana29.deliverymanagement.repository.MenuRepository;
import com.ana29.deliverymanagement.repository.OrderRepository;
import com.ana29.deliverymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final MenuRepository menuRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	public CreateOrderResponseDto createOrder(CreateOrderRequestDto requestDto, String userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("User not found"));

		Menu menu = menuRepository.findById(requestDto.menuId())
			.orElseThrow(() -> new RuntimeException("Menu not found"));

		Order order = Order.builder()
			.user(user)
			.menu(menu)
			.quantity(requestDto.quantity())
			.orderRequest(requestDto.orderRequest())
			.build();

		Order savedOrder = orderRepository.save(order);

		return new CreateOrderResponseDto(savedOrder.getId(), savedOrder.getTotalAmount());
	}
}

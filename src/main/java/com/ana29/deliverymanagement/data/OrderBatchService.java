//package com.ana29.deliverymanagement.order.service;
//
//import com.ana29.deliverymanagement.global.constant.OrderStatusEnum;
//import com.ana29.deliverymanagement.global.constant.OrderTypeEnum;
//import com.ana29.deliverymanagement.global.constant.PaymentStatusEnum;
//import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
//import com.ana29.deliverymanagement.order.entity.Order;
//import com.ana29.deliverymanagement.order.entity.Payment;
//import com.ana29.deliverymanagement.order.repository.OrderRepository;
//import com.ana29.deliverymanagement.order.repository.PaymentRepository;
//import com.ana29.deliverymanagement.restaurant.entity.Menu;
//import com.ana29.deliverymanagement.restaurant.repository.MenuRepository;
//import com.ana29.deliverymanagement.user.entity.User;
//import com.ana29.deliverymanagement.user.repository.UserRepository;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.UUID;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class OrderBatchService {
//	private final UserRepository userRepository;
//	private final MenuRepository menuRepository;
//	private final OrderRepository orderRepository;
//	private final PaymentRepository paymentRepository;
//
//	public void generateTestOrders(int batchSize) {
//		// 미리 데이터를 로드
//		List<User> users = userRepository.findAll();
//		List<Menu> menus = menuRepository.findAll();
//		Random random = new Random();
//
//		List<Order> orders = new ArrayList<>();
//		List<Payment> payments = new ArrayList<>();
//
//		for (int i = 0; i < batchSize; i++) {
//			User randomUser = users.get(random.nextInt(users.size()));
//			Menu randomMenu = menus.get(random.nextInt(menus.size()));
//
//			int quantity = random.nextInt(1, 5);  // 1~4개 랜덤 주문
//
//			Order order = Order.builder()
//				.user(randomUser)
//				.menu(randomMenu)
//				.quantity(quantity)
//				.totalPrice(randomMenu.getPrice() * quantity)
//				.orderStatus(OrderStatusEnum.PAID)  // 또는 랜덤 상태
//				.orderType(OrderTypeEnum.ONLINE)    // 또는 랜덤 타입
//				.orderRequest("테스트 주문 #" + i)
//				.build();
//
//			Payment payment = Payment.builder()
//				.order(order)
//				.totalPrice(order.getTotalPrice())
//				.paymentStatus(PaymentStatusEnum.COMPLETED)
//				.paymentType(PaymentTypeEnum.CREDIT_CARD)
//				.externalPaymentId(UUID.randomUUID())
//				.build();
//
//			orders.add(order);
//			payments.add(payment);
//
//			// 일정 크기마다 벌크 저장
//			if (orders.size() >= 1000) {
//				orderRepository.saveAll(orders);
//				paymentRepository.saveAll(payments);
//				orders.clear();
//				payments.clear();
//			}
//		}
//
//		// 남은 데이터 저장
//		if (!orders.isEmpty()) {
//			orderRepository.saveAll(orders);
//			paymentRepository.saveAll(payments);
//		}
//	}
//}
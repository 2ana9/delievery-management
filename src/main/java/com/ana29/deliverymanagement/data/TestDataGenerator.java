//package com.ana29.deliverymanagement.data;
//
//import com.ana29.deliverymanagement.order.service.OrderBatchService;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class TestDataGenerator {
//	private final OrderBatchService orderBatchService;
//
//	@PostConstruct
//	public void generateData() {
//		orderBatchService.generateTestOrders(1_000_000);  // 100만건 생성
//	}
//}
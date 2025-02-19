package com.ana29.deliverymanagement.order.entity;

import com.ana29.deliverymanagement.global.constant.PaymentStatusEnum;
import com.ana29.deliverymanagement.global.constant.PaymentTypeEnum;
import com.ana29.deliverymanagement.global.entity.Timestamped;
import com.ana29.deliverymanagement.order.dto.PaymentResultDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_payment")
public class Payment extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "payment_id", nullable = false)
	private UUID id;

	@Column(nullable = false)
	private Long totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatusEnum paymentStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentTypeEnum paymentType;

	private String errorMessage;

	@Column
	private UUID externalPaymentId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	private LocalDateTime refundedAt;

	public void refund(){
		this.refundedAt = LocalDateTime.now();
		this.paymentStatus = PaymentStatusEnum.REFUNDED;
	}

	public static Payment from(Order order, PaymentResultDto result) {
		return Payment.builder()
			.totalPrice(order.getTotalPrice())
			.order(order)
			.paymentType(result.paymentTypeEnum())
			.paymentStatus(
				result.isSuccess() ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.FAILED)
			.externalPaymentId(result.externalPaymentId())
			.errorMessage(result.errorMessage())
			.build();
	}
}

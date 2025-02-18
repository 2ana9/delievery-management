package com.ana29.deliverymanagement.order.repository;

import com.ana29.deliverymanagement.order.entity.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}

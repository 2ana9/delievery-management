package com.ana29.deliverymanagement.repository;

import com.ana29.deliverymanagement.entity.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}

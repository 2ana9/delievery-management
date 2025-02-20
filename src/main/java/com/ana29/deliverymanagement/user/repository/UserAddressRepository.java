package com.ana29.deliverymanagement.user.repository;

import com.ana29.deliverymanagement.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, String> {
//    List<UserAddress> findByUser(User user);

    Optional<UserAddress> findById(String id);
}
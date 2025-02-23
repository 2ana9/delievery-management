package com.ana29.deliverymanagement.user.repository;

import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.user.entity.UserAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID>, UserAddressRepositoryCustom {
    List<UserAddress> findByUserAndIsDeletedFalse(User user);
    Optional<UserAddress> findByIdAndUserIdAndIsDeletedFalse(UUID id, String userId);
    Optional<UserAddress> findByUserAndDefaultAddressTrue(User user);
}
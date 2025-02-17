package com.ana29.deliverymanagement.user.repository;

import com.ana29.deliverymanagement.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByPhone(String phone);

//    Optional<User> findByKakaoId(Long kakaoId);
}
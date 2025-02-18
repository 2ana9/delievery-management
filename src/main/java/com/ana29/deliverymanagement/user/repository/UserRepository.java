package com.ana29.deliverymanagement.user.repository;

import com.ana29.deliverymanagement.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByPhone(String phone);
    @Query("select u from User u where u.Id = :id or u.email = :email or u.nickname = :nickname or u.phone = :phone")
    Optional<User> findAnyDuplicate(@Param("id") String id,
                                    @Param("email") String email,
                                    @Param("nickname") String nickname,
                                    @Param("phone") String phone);

//    Optional<User> findByKakaoId(Long kakaoId);
}
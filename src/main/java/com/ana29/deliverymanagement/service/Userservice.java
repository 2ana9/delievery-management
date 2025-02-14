package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.config.admin.AdminConfig;
import com.ana29.deliverymanagement.entity.User;
import com.ana29.deliverymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ana29.deliverymanagement.constant.UserRoleEnum;
import com.ana29.deliverymanagement.dto.SignupRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Userservice {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AdminConfig adminConfig;

    public void signup(SignupRequestDto requestDto, BindingResult bindingResult) {
        String username = requestDto.getId();
        String password = passwordEncoder.encode(requestDto.getPassword());

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/users/sign-up";
        }

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findById(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!adminConfig.getAdminSignupKey().equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 사용자 등록
        User user = new User(username,);
        userRepository.save(user);
    }
}

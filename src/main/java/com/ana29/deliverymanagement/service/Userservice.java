package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.config.admin.AdminConfig;
import com.ana29.deliverymanagement.constant.SignupConfig;
import com.ana29.deliverymanagement.constant.UserRoleEnum;
import com.ana29.deliverymanagement.dto.SignupRequestDto;
import com.ana29.deliverymanagement.entity.User;
import com.ana29.deliverymanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;

    public String signup(SignupRequestDto requestDto, BindingResult bindingResult) {
//        에러 발생 시 기존 회원가입 url 리다이렉트 하는 global handler 필요
        // 1. 바인딩 에러 체크 (컨트롤러에서 @Valid를 사용했을 때의 추가 검증)
        checkFieldErrors(bindingResult);

        // 2. 사용자명 검증 및 중복 체크
        validateUsername(requestDto.getId());
        checkUsernameDuplicate(requestDto.getId());

        // 3. 이메일 검증 및 중복 체크
        validateEmail(requestDto.getEmail());
        checkEmailDuplicate(requestDto.getEmail());

        // 4. 닉네임 정규식 검사
        validateNickname(requestDto.getNickname());
        checkNicknameDuplicate(requestDto.getNickname());

        // 5. 전화번호 정규식 검사
        validatePhone(requestDto.getPhone());
        checkPhoneDuplicate(requestDto.getPhone());

        // 6. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 7. 사용자 역할 확인 (관리자 요청인 경우 관리자 키 검증)
        UserRoleEnum role = checkUserRole(requestDto);

        // 8. 상세 주소 확인
        String currentAddress = checkCurrentAddress(requestDto.getCurrentAddress());

        // 8. 사용자 등록 (여기서는 필요한 필드만 사용 - 엔티티 수정은 불가능하므로 DTO와 맞춰서 작성)
        User user = User.builder()
                .Id(requestDto.getId())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .phone(requestDto.getPhone())
                .role(role)
                .currentAddress(currentAddress)
                .build();
        System.out.println("user id= " + user.getId());
        System.out.println("user phone= " + user.getPhone());
        System.out.println("user role= " + user.getRole());
        System.out.println("admin token= " + requestDto.getAdminToken());

        userRepository.save(user);

        return "redirect:/api/users/sign-in";
    }

    /**
     * 🔹 바인딩 에러 체크
     */
    private void checkFieldErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("입력 오류: ");
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error("{} 필드 오류: {}", fieldError.getField(), fieldError.getDefaultMessage());
                errorMessage.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append("; ");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }


    /**
     * 🔹 사용자명(아이디) 정규식 검사
     */
    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자명을 입력해야 합니다.");
        }
        if (!SignupConfig.USERNAME_PATTERN.getPattern().matcher(username).matches()) {
            throw new IllegalArgumentException("사용자명은 1~50자의 영문과 숫자만 가능합니다.");
        }
    }

    /**
     * 🔹 중복된 사용자명(아이디) 체크
     */
    private void checkUsernameDuplicate(String username) {
        Optional<User> checkUsername = userRepository.findById(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
    }

    /**
     * 🔹 이메일 정규식 검사
     */
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해야 합니다.");
        }
        if (!SignupConfig.EMAIL_PATTERN.getPattern().matcher(email).matches()) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    /**
     * 🔹 중복된 이메일 체크
     */
    private void checkEmailDuplicate(String email) {
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }
    }

    /**
     * 🔹 닉네임 정규식 검사
     */
    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임을 입력해야 합니다.");
        }
        if (!SignupConfig.NICKNAME_PATTERN.getPattern().matcher(nickname).matches()) {
            throw new IllegalArgumentException("닉네임은 2~20자의 한글, 영문, 숫자만 가능합니다.");
        }
    }

    private void checkNicknameDuplicate(String nickname) {
        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임 입니다.");
        }
    }

    /**
     * 🔹 전화번호 정규식 검사
     */
    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("전화번호를 입력해야 합니다.");
        }
        if (!SignupConfig.PHONE_PATTERN.getPattern().matcher(phone).matches()) {
            throw new IllegalArgumentException("전화번호는 010-XXXX-XXXX 또는 010XXXXXXXX 형식이어야 합니다.");
        }
    }

    private void checkPhoneDuplicate(String phone) {
        Optional<User> checkPhone = userRepository.findByPhone(phone);
        if (checkPhone.isPresent()) {
            throw new IllegalArgumentException("중복된 전화번호 입니다.");
        }
    }

    /**
     * 🔹 사용자 역할 확인 (관리자 요청인 경우 관리자 키 검증)
     */
    private UserRoleEnum checkUserRole(SignupRequestDto requestDto) {
        if (adminConfig.getAdminSignupKey().equals(requestDto.getAdminToken())) {
            return UserRoleEnum.ADMIN;
//                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
        } else {
            return UserRoleEnum.USER;
        }
    }


    private String checkCurrentAddress(String currentAddress) {
        if (currentAddress == null || currentAddress.trim().isEmpty()) {
            return null;
        }
        return currentAddress;
    }
}

package com.ana29.deliverymanagement.user.service;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.admin.AdminConfig;
import com.ana29.deliverymanagement.security.constant.user.SignupConfig;
import com.ana29.deliverymanagement.security.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.security.jwt.TokenBlacklist;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
import com.ana29.deliverymanagement.user.dto.UserInfoDto;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;
    private final JPAQueryFactory queryFactory;

    private final JwtUtil jwtUtil;

    public String signup(SignupRequestDto requestDto) {

//        QUser user = QUser.user;
//        BooleanExpression duplicatePredicate =
//                user.Id.eq(requestDto.getId())
//                        .or(user.email.eq(requestDto.getEmail()))
//                        .or(user.nickname.eq(requestDto.getNickname()))
//                        .or(user.phone.eq(requestDto.getPhone()));
//
//        Long duplicateCount = queryFactory.select(user.count())
//                .from(user)
//                .where(duplicatePredicate)
//                .fetchOne();
//
//        if (duplicateCount != null && duplicateCount > 0) {
//            // 중복된 데이터가 있는 경우 적절한 예외를 던집니다.
//            throw new RuntimeException("아이디, 이메일, 닉네임, 전화번호 중 하나 이상이 이미 존재합니다.");
//        }

//        에러 발생 시 기존 회원가입 url 리다이렉트 하는 global handler 필요
        // 1. 바인딩 에러 체크 (컨트롤러에서 @Valid를 사용했을 때의 추가 검증)
//        checkFieldErrors(bindingResult);

        // 2. 사용자명 검증 및 중복 체크
        validateUsername(requestDto.getId());

        // 3. 이메일 검증 및 중복 체크
        validateEmail(requestDto.getEmail());

        // 4. 닉네임 정규식 검사
        validateNickname(requestDto.getNickname());


        // 5. 전화번호 정규식 검사
        validatePhone(requestDto.getPhone());

        // 6. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
//        String encodedPassword = requestDto.getPassword();


        // 7. 사용자 역할 확인 (관리자 요청인 경우 관리자 키 검증)
        UserRoleEnum role = checkUserRole(requestDto);

        // 8. 상세 주소 확인
        String currentAddress = checkCurrentAddress(requestDto.getCurrentAddress());

        // 8. 사용자 등록 (여기서는 필요한 필드만 사용 - 엔티티 수정은 불가능하므로 DTO와 맞춰서 작성)
        User uuser = User.builder()
                .Id(requestDto.getId())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .phone(requestDto.getPhone())
                .role(role)
                .currentAddress(currentAddress)
                .build();
        log.info("user id= " + uuser.getId());
        log.info("user phone= " + uuser.getPhone());
        log.info("user role= " + uuser.getRole());
        log.info("admin token= " + requestDto.getAdminToken());

        userRepository.save(uuser);

        return "/api/users/sign-in";
    }
    public String signOut(HttpServletRequest request) {
        String token = jwtUtil.getJwtFromHeader(request);
        log.info("Sign Out Token Value   : " + token);

        if (token != null && !token.isEmpty()) {
            TokenBlacklist.addToken(token);
        } else {
            throw new IllegalArgumentException("Token is Empty, 유효하지 않은 접근입니다.");
        }
        SecurityContextHolder.clearContext();
        return "/api/users/sign-in";
    }

    public List<UserInfoDto> getUserInfo(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        boolean isAdmin = (user.getRole() == UserRoleEnum.ADMIN);

        List<UserInfoDto> userInfoDtoList = new ArrayList<>();

        if (isAdmin) {
            // Admin이면 모든 유저 정보를 가져옴 (페이징 적용)
            List<User> userList = userRepository.findAll(PageRequest.of(0, 10)).getContent();

            // User -> UserInfoDto 변환하여 리스트에 추가
            userInfoDtoList = userList.stream()
                    .map(u -> new UserInfoDto(u.getId(), u.getNickname(), u.getEmail(), u.getPhone(), true))
                    .collect(Collectors.toList());
        } else {
            // 일반 사용자는 자신의 정보만 반환
            userInfoDtoList.add(new UserInfoDto(user.getId(), user.getNickname(), user.getEmail(), user.getPhone(), false));
        }

        return userInfoDtoList;
    }


    /**
     * JWT를 통해 인증된 사용자(UserDetailsImpl)를 기반으로,
     * 추가로 전달된 업데이트 DTO의 정보로 회원 정보를 수정한 후,
     * 수정된 정보를 UserInfoDto로 반환합니다.
     */


    @Transactional
    public List<UserInfoDto> modifyUserInfo(UserDetailsImpl userDetails, UpdateRequestDto updateDto) {
        // JWT로부터 현재 로그인한 사용자 엔티티 가져오기
        User user = userDetails.getUser();

//        setter 사용 지양

        // 업데이트 DTO의 정보로 필드 수정
        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());
        user.setPhone(updateDto.getPhone());
        if (updateDto.getCurrentAddress() != null) {
            user.setCurrentAddress(updateDto.getCurrentAddress());
        }

        // DB에 변경 사항 저장
        userRepository.save(user);

        // 수정된 회원 정보를 DTO로 변환하여 반환 (여기서는 단일 객체를 리스트로 감싸서 반환)
        boolean isAdmin = (user.getRole() == UserRoleEnum.ADMIN);
        UserInfoDto updatedInfo = new UserInfoDto(user.getId(), user.getNickname(), user.getEmail(), user.getPhone(), isAdmin);
        return List.of(updatedInfo);
    }

    @Transactional
    public void deleteUser(UserDetailsImpl userDetails, UpdateRequestDto updateDto) {
        User user = userDetails.getUser();
        userRepository.delete(user);
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

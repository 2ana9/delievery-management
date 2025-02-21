package com.ana29.deliverymanagement.user.service;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.AuthorityConfig;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.security.jwt.TokenBlacklist;
import com.ana29.deliverymanagement.security.service.SecurityContextRedisService;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
import com.ana29.deliverymanagement.user.dto.UserInfoDto;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AuthorityConfig authorityConfig;
    private final JwtUtil jwtUtil;
    private final SecurityContextRedisService redisService; // ✅ Redis 서비스 추가

    @Transactional
    public String signup(SignupRequestDto requestDto) {
        validateDuplicateValue(requestDto);
        User savedUser = userRepository.save(createUserDto(requestDto));


        // ✅ UserDetails 생성
        UserDetailsImpl userDetails = new UserDetailsImpl(savedUser);

        // ✅ Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,     // UserDetails 객체
                savedUser.getPassword(),            // 비밀번호 (null로 설정 가능)
                userDetails.getAuthorities() // 권한 리스트
        );

        // ✅ SecurityContext에 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        redisService.saveUserDetailsToRedis(); // 🔹 Redis 저장

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

        // ✅ 로그아웃 시 Redis에서 삭제
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        redisService.removeUserDetailsFromRedis(userDetails.getUsername());

        SecurityContextHolder.clearContext();
        return "/api/users/sign-in";
    }

    public List<UserInfoDto> getUserInfo(UserDetailsImpl userDetails, int page, int size, String sortBy, boolean isAsc) {
        boolean isAdmin = (userDetails.getRole() == UserRoleEnum.MASTER || userDetails.getRole() == UserRoleEnum.MANAGER);

        List<UserInfoDto> userInfoDtoList = new ArrayList<>();

        if (isAdmin) {
            List<User> userList = userInfoPaging(page, size, sortBy, isAsc);
            userInfoDtoList = userList.stream()
                    .map(u -> new UserInfoDto(u.getId(), u.getNickname(), u.getEmail(), u.getPhone(), u.getRole()))
                    .collect(Collectors.toList());
        } else {
            userInfoDtoList.add(new UserInfoDto(userDetails.getUsername(), userDetails.getNickname(),
                    userDetails.getEmail(), userDetails.getPhone(), userDetails.getRole()));
        }

        return userInfoDtoList;
    }

    @Transactional
    public UserInfoDto modifyUserInfo(UserDetailsImpl userDetails, UpdateRequestDto updateDto) {
        validateDuplicateValue(updateDto);

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userDetails.getId()));

        userRepository.save(user);

        // ✅ 사용자 정보 변경 후 Redis 업데이트
        redisService.saveUserDetailsToRedis();

        return new UserInfoDto(user.getId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getRole());
    }

    @Transactional
    public void deleteUser(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userDetails.getUsername()));

        // ✅ 사용자 삭제 전 Redis에서 제거
        redisService.removeUserDetailsFromRedis(userDetails.getUsername());

        userRepository.delete(user);
    }

    private void validateDuplicateValue(SignupRequestDto requestDto) {
        Optional<User> duplicateUserOpt = userRepository.findAnyDuplicate(
                requestDto.getId(),
                requestDto.getEmail(),
                requestDto.getNickname(),
                requestDto.getPhone()
        );
        if (duplicateUserOpt.isPresent()) {
            getUser(requestDto, duplicateUserOpt);
        }
    }

    private void validateDuplicateValue(UpdateRequestDto requestDto) {
        Optional<User> duplicateUserOpt = userRepository.findAnyDuplicate(
                requestDto.getEmail(),
                requestDto.getNickname(),
                requestDto.getPhone()
        );
        if (duplicateUserOpt.isPresent()) {
            getUser(requestDto, duplicateUserOpt);
        }
    }

    private void getUser(SignupRequestDto requestDto, Optional<User> duplicateUserOpt) {
        User duplicateUser = duplicateUserOpt.get();
        if (duplicateUser.getId().equals(requestDto.getId())) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        if (duplicateUser.getEmail().equals(requestDto.getEmail())) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }
        if (duplicateUser.getNickname().equals(requestDto.getNickname())) {
            throw new IllegalArgumentException("중복된 닉네임 입니다.");
        }
        if (duplicateUser.getPhone().equals(requestDto.getPhone())) {
            throw new IllegalArgumentException("중복된 전화번호 입니다.");
        }
    }

    private void getUser(UpdateRequestDto requestDto, Optional<User> duplicateUserOpt) {
        User duplicateUser = duplicateUserOpt.get();
        if (duplicateUser.getEmail().equals(requestDto.getEmail())) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }
        if (duplicateUser.getNickname().equals(requestDto.getNickname())) {
            throw new IllegalArgumentException("중복된 닉네임 입니다.");
        }
        if (duplicateUser.getPhone().equals(requestDto.getPhone())) {
            throw new IllegalArgumentException("중복된 전화번호 입니다.");
        }
    }

    private User createUserDto(SignupRequestDto requestDto) {
        return User.builder()
                .Id(requestDto.getId())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .phone(requestDto.getPhone())
                .role(checkUserRole(requestDto))
                .build();
    }

    private UserRoleEnum checkUserRole(SignupRequestDto requestDto) {
        if (authorityConfig.getMasterSignupKey().equals(requestDto.getTokenValue())) {
            return UserRoleEnum.MASTER;
        } else if (authorityConfig.getManagerSignupKey().equals(requestDto.getTokenValue())) {
            return UserRoleEnum.MANAGER;
        } else if (authorityConfig.getOwnerSignupKey().equals(requestDto.getTokenValue())) {
            return UserRoleEnum.OWNER;
        } else {
            return UserRoleEnum.CUSTOMER;
        }
    }

    private List<User> userInfoPaging(int page, int size, String sortBy, boolean isAsc) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable).getContent();
    }
}

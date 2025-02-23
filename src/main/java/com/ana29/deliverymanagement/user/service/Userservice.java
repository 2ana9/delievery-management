package com.ana29.deliverymanagement.user.service;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.AuthorityConfig;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.security.jwt.RedisTokenBlacklist;
import com.ana29.deliverymanagement.security.service.SecurityContextRedisService;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
import com.ana29.deliverymanagement.user.dto.UserInfoDto;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final RedisTokenBlacklist redisTokenBlacklist;

    @Transactional
    public String signup(SignupRequestDto requestDto) {
        validateDuplicateValue(requestDto);
        userRepository.save(createUserDto(requestDto));
        return "/api/users/sign-in";
    }

    public String signOut(UserDetailsImpl userDetails, HttpServletRequest request) {
        String token = jwtUtil.getJwtFromHeader(request);
        log.info("Sign Out Token Value   : " + token);

        validTokenBlackList(token);

        // 로그아웃 시, Redis에서 사용자 정보 삭제
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

        //닉네임, 이메일, 전화번호만 수정
        User user = modifyUser(userDetails, updateDto);

        // 사용자 정보 변경 후, Redis에 저장된 정보를 업데이트
        redisService.saveUserDetailsToRedis();

        return new UserInfoDto(user.getId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getRole());
    }

    @Transactional
    public void deleteUser(UserDetailsImpl userDetails) {
        User user = userRepository.findByIdAndIsDeletedFalse(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userDetails.getUsername()));

        // 삭제 전 Redis에서 사용자 정보 제거
        redisService.removeUserDetailsFromRedis(userDetails.getUsername());
        user.softDelete(userDetails.getUsername());
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
        User user = new User(requestDto.getId(),
                requestDto.getNickname(),
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getPhone(),
                checkUserRole(requestDto.getTokenValue()));
        user.setCreatedBy(user.getId());
        return user;
    }

    private UserRoleEnum checkUserRole(String tokenValue) {
        if (authorityConfig.getMasterSignupKey().equals(tokenValue)) {
            return UserRoleEnum.MASTER;
        } else if (authorityConfig.getManagerSignupKey().equals(tokenValue)) {
            return UserRoleEnum.MANAGER;
        } else if (authorityConfig.getOwnerSignupKey().equals(tokenValue)) {
            return UserRoleEnum.OWNER;
        } else {
            log.info(tokenValue);
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

    private User modifyUser(UserDetailsImpl userDetails, UpdateRequestDto updateDto) {
        User user = userRepository.findByIdAndIsDeletedFalse(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userDetails.getId()));

//        닉네임, 이메일, 전화번호
        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());
        user.setPhone(updateDto.getPhone());
        user.setUpdatedBy(userDetails.getUsername());

        return user;
    }

    private void validTokenBlackList(String token) {
        if (token != null && !token.isEmpty()) {
            // 토큰의 만료 시간(exp)을 클레임에서 추출
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            long expirationTimeMillis = claims.getExpiration().getTime();
            long tokenExpirationMillis = expirationTimeMillis - System.currentTimeMillis();

            // tokenExpirationMillis가 음수가 아니고, 남은 유효시간이 있다면 블랙리스트에 추가
            if (tokenExpirationMillis > 0) {
                redisTokenBlacklist.addToken(token, tokenExpirationMillis);
            }
        } else {
            throw new IllegalArgumentException("Token is Empty, 유효하지 않은 접근입니다.");
        }
    }
}

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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        userRepository.save(createUserDto(requestDto));
        return "/api/users/sign-in";
    }

    public String signOut(UserDetailsImpl userDetails, HttpServletRequest request) {
        String token = jwtUtil.getJwtFromHeader(request);
        log.info("Sign Out Token Value   : " + token);

        if (token != null && !token.isEmpty()) {
            TokenBlacklist.addToken(token);
        } else {
            throw new IllegalArgumentException("Token is Empty, 유효하지 않은 접근입니다.");
        }
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
    public UpdateRequestDto modifyUserInfo(UserDetailsImpl userDetails, UpdateRequestDto updateDto) {
        validateDuplicateValue(updateDto);

        //닉네임, 이메일, 전화번호
        modifyUser(userDetails, updateDto);

        // 사용자 정보 변경 후, Redis에 저장된 정보를 업데이트
        redisService.saveUserDetailsToRedis();

        return updateDto;
    }

    @Transactional
    public void deleteUser(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userDetails.getUsername()));

        // 삭제 전 Redis에서 사용자 정보 제거
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
                .role(checkUserRole(requestDto.getTokenValue()))
                .build();
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

    private void modifyUser(UserDetailsImpl userDetails, UpdateRequestDto updateDto){
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userDetails.getId()));

//        닉네임, 이메일, 전화번호
        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());
        user.setPhone(updateDto.getPhone());

    }
}

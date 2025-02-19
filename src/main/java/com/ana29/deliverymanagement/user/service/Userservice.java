package com.ana29.deliverymanagement.user.service;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.AuthorityConfig;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.security.jwt.TokenBlacklist;
import com.ana29.deliverymanagement.user.constant.user.UserRoleEnum;
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

    @Transactional
    public String signup(SignupRequestDto requestDto) {
        // 중복 체크: 한 번의 쿼리로 모든 필드를 동시에 확인
        validateDuplicateValue(requestDto);
        userRepository.save(createUserDto(requestDto));
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

    public List<UserInfoDto> getUserInfo(UserDetailsImpl userDetails, int page, int size, String sortBy, boolean isAsc) {
        boolean isAdmin = (userDetails.getUser().getRole() == UserRoleEnum.MASTER || userDetails.getUser().getRole() == UserRoleEnum.MANAGER);

        List<UserInfoDto> userInfoDtoList = new ArrayList<>();

        if (isAdmin) {
            // Admin이면 모든 유저 정보를 가져옴 (페이징 적용)
            List<User> userList = userInfoPaging(page, size, sortBy, isAsc);

            // User -> UserInfoDto 변환하여 리스트에 추가
            userInfoDtoList = userList.stream()
                    .map(u -> new UserInfoDto(u.getId(), u.getNickname(), u.getEmail(), u.getPhone(), u.getRole()))
                    .collect(Collectors.toList());
        } else {
            // 일반 사용자는 자신의 정보만 반환
            userInfoDtoList.add(new UserInfoDto(userDetails.getUser().getId(), userDetails.getUser().getNickname(),
                    userDetails.getUser().getEmail(), userDetails.getUser().getPhone(), userDetails.getUser().getRole()));
        }

        return userInfoDtoList;
    }


    /**
     * JWT를 통해 인증된 사용자(UserDetailsImpl)를 기반으로,
     * 추가로 전달된 업데이트 DTO의 정보로 회원 정보를 수정한 후,
     * 수정된 정보를 UserInfoDto로 반환합니다.
     */



    @Transactional
    public UserInfoDto modifyUserInfo(UserDetailsImpl userDetails, UpdateRequestDto updateDto) {
        // JWT로부터 현재 로그인한 사용자 엔티티 가져오기
        User user = userDetails.getUser();

        // 업데이트 DTO의 정보로 필드 수정
        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());
        user.setPhone(updateDto.getPhone());

        // DB에 변경 사항 저장
        userRepository.save(user);

        // 수정된 회원 정보를 DTO로 변환하여 반환
        return new UserInfoDto(user.getId(), user.getNickname(), user.getEmail(), user.getPhone(), user.getRole());
    }

    @Transactional
    public void deleteUser(UserDetailsImpl userDetails) {
        userRepository.delete(userDetails.getUser());
    }


    private void validateDuplicateValue(SignupRequestDto requestDto){
        // 중복 체크: 한 번의 쿼리로 모든 필드를 동시에 확인
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

    private void getUser(SignupRequestDto requestDto, Optional<User> duplicateUserOpt) {
        User duplicateUser = duplicateUserOpt.get();
        // 중복된 필드를 확인하고, 해당하는 예외 메시지를 던집니다.
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

    private User createUserDto(SignupRequestDto requestDto) {
        // 8. 사용자 등록 (여기서는 필요한 필드만 사용 - 엔티티 수정은 불가능하므로 DTO와 맞춰서 작성)
        return User.builder()
                .Id(requestDto.getId())
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword())) // 비밀번호 암호화
                .phone(requestDto.getPhone())
                .role(checkUserRole(requestDto)) // 유저 권한 부여
//                .currentAddress(checkCurrentAddress(requestDto.getCurrentAddress())) // 상세 주소 확인
                .build();
    }


     // 사용자 역할 확인 (관리자 요청인 경우 관리자 키 검증)
    private UserRoleEnum checkUserRole(SignupRequestDto requestDto) {
        if (authorityConfig.getMasterSignupKey().equals(requestDto.getTokenValue())) {
            return UserRoleEnum.MASTER;
        } else if (authorityConfig.getManagerSignupKey().equals(requestDto.getTokenValue())){
            return UserRoleEnum.MANAGER;
        } else if (authorityConfig.getOwnerSignupKey().equals(requestDto.getTokenValue())) {
            return UserRoleEnum.OWNER;
        }else {
            return UserRoleEnum.CUSTOMER;
        }
    }


    private List<User> userInfoPaging(int page, int size, String sortBy, boolean isAsc){
        // 10, 30, 50 중에서 선택된 값만 허용
        if (size != 10 && size != 30 && size != 50) {
            size = 10; // 기본값
        }
        // 정렬 기준 설정 (기본: 생성일)
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        // 페이징 및 정렬 적용하여 유저 리스트 조회
        Pageable pageable = PageRequest.of(page, size, sort);

        // 페이징 및 정렬 적용하여 유저 리스트 조회
        return userRepository.findAll(pageable).getContent();
    }
}

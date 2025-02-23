package com.ana29.deliverymanagement.externalApi.kakao.service;

import com.ana29.deliverymanagement.externalApi.kakao.dto.KakaoTokenResponseDto;
import com.ana29.deliverymanagement.externalApi.kakao.dto.KakaoUserInfoDto;
import com.ana29.deliverymanagement.externalApi.kakao.feign.KakaoClient;
import com.ana29.deliverymanagement.externalApi.kakao.feign.KakaoUserClient;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j(topic = "KAKAO Login")
@Service
@Getter
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoClient kakaoClient;
    private final KakaoUserClient kakaoUserClient;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public String kakaoLogin(String code) throws JsonProcessingException {
        log.info("kakaoLogin Service Method, code: {}", code);

        // 1. 인가 코드를 사용하여 액세스 토큰 요청
        KakaoTokenResponseDto tokenResponse = kakaoClient.getToken(
                "authorization_code",
                kakaoClientId,
                kakaoRedirectUri,
                code
        );

        String accessToken = tokenResponse.getAccessToken();

        // 2. 액세스 토큰으로 카카오 API 호출하여 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoUserClient.getKakaoUserInfo("Bearer " + accessToken);


        // 3. 필요시 회원가입 처리: 이미 등록된 사용자가 있으면 반환, 없으면 새로 등록
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
        log.info("Kakao User: {}", kakaoUser);

        // 4. JWT 토큰 생성
        String rawToken = jwtUtil.createToken(kakaoUser.getId(), kakaoUser.getRole());
        log.info("Raw JWT Token: {}", rawToken);

        // "Bearer " 접두어가 붙어있다면 제거하여 반환
        String token = rawToken.startsWith("Bearer ")
                ? rawToken.substring("Bearer ".length())
                : rawToken;
        log.info("Final JWT Token (without Bearer): {}", token);
        return token;
    }

    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // 카카오 사용자의 고유 식별값에 "kakao"를 붙여 사용자 ID로 사용
        String kakaoUserId = String.valueOf(kakaoUserInfo.getId()) + "kakao";
        return userRepository.findByIdAndIsDeletedFalse(kakaoUserId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .Id(kakaoUserId)
                            .nickname(kakaoUserInfo.getNickname() + "kakao")
                            .email(kakaoUserInfo.getEmail())
                            .password(passwordEncoder.encode("kakao1234"))
                            .phone("010-1111-1111")
                            .role(UserRoleEnum.CUSTOMER)
                            .build();
                    newUser.setCreatedBy(String.valueOf(kakaoUserInfo.getId()));
                    return userRepository.save(newUser);
                });
    }
}


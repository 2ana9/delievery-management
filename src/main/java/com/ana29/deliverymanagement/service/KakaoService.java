package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.constant.UserRoleEnum;
import com.ana29.deliverymanagement.entity.User;
import com.ana29.deliverymanagement.kakao.*;
import com.ana29.deliverymanagement.jwt.JwtUtil;
import com.ana29.deliverymanagement.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoClient kakaoClient;
    private final KakaoUserClient kakaoUserClient;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-url}")
    private String kakaoRedirectUri;

    public String kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        KakaoTokenResponse tokenResponse = kakaoClient.getToken(
                "authorization_code",
                kakaoClientId,
                kakaoRedirectUri,
                code
        );
        String accessToken = tokenResponse.getAccessToken();

        // 2. 토큰으로 카카오 API 호출하여 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoUserClient.getKakaoUserInfo("Bearer " + accessToken);

        // 3. 필요시 회원가입 처리
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 4. JWT 토큰 생성하여 반환
        return jwtUtil.createToken(kakaoUser.getId(), kakaoUser.getRole());
    }

    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        return userRepository.findById(String.valueOf(kakaoUserInfo.getId()))
                .orElseGet(() -> {
                    User newUser = new User(
                            String.valueOf(kakaoUserInfo.getId()),
                            kakaoUserInfo.getNickname(),
                            kakaoUserInfo.getEmail(),
                            "",
                            UserRoleEnum.USER
                    );
                    return userRepository.save(newUser);
                });
    }
}

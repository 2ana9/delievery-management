package com.ana29.deliverymanagement.externalApi.kakao;

import com.ana29.deliverymanagement.externalApi.kakao.feign.KakaoClient;
import com.ana29.deliverymanagement.externalApi.kakao.feign.KakaoUserClient;
import com.ana29.deliverymanagement.user.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.user.repository.UserRepository;
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

        log.info("kakaoLogin Service Method");

        // 1. "인가 코드"로 "액세스 토큰" 요청
        KakaoTokenResponseDto tokenResponse = kakaoClient.getToken(
                "authorization_code",
                kakaoClientId,
                kakaoRedirectUri,
                code
        );
        String accessToken = tokenResponse.getAccessToken();

        // 2. 토큰으로 카카오 API 호출하여 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoUserClient.getKakaoUserInfo("Bearer " + accessToken);

        log.info(String.valueOf(kakaoUserInfo.getId()));
        log.info(String.valueOf(kakaoUserInfo.getNickname()));
        log.info(String.valueOf(kakaoUserInfo.getEmail()));

        // 3. 필요시 회원가입 처리
//        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

//        3-1. 임시 테스트 데이터 생성
        User kakaoUser = User.builder()
                .Id(String.valueOf(kakaoUserInfo.getId()) + "kakao")
                .nickname(kakaoUserInfo.getNickname() + "kakao")
                .email(kakaoUserInfo.getEmail())
                .password("kakao1234")
                .phone("010-1111-1111")
                .role(UserRoleEnum.CUSTOMER)
                .build();
//            3-2. 임시 데이터 저장 테스트
        userRepository.save(kakaoUser);

        log.info("kakaoUser : " + kakaoUser.toString());
        // 4. JWT 토큰 생성하여 반환
        return jwtUtil.createToken(kakaoUser.getId(), kakaoUser.getRole());
    }

//    private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
//        return userRepository.findById(String.valueOf(kakaoUserInfo.getId()))
//                .orElseGet(() -> {
//                    User newUser = new User (
//                            String.valueOf(kakaoUserInfo.getId()),
//                            kakaoUserInfo.getNickname(),
//                            kakaoUserInfo.getEmail(),
//                            "",
//                            UserRoleEnum.USER
//                    );
//                    return userRepository.save(newUser);
//                });
//    }
}

//package com.ana29.deliverymanagement.mvc;
//
//import com.ana29.deliverymanagement.security.constant.user.UserRoleEnum;
//import com.ana29.deliverymanagement.user.entity.User;
//import com.ana29.deliverymanagement.security.jwt.JwtUtil;
//import com.ana29.deliverymanagement.externalApi.kakao.feign.KakaoClient;
//import com.ana29.deliverymanagement.externalApi.kakao.KakaoTokenResponse;
//import com.ana29.deliverymanagement.externalApi.kakao.feign.KakaoUserClient;
//import com.ana29.deliverymanagement.externalApi.kakao.KakaoUserInfoDto;
//import com.ana29.deliverymanagement.user.repository.UserRepository;
//import com.ana29.deliverymanagement.externalApi.kakao.KakaoService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class KakaoServiceTest {
//
//    @Mock
//    private KakaoClient kakaoClient;
//
//    @Mock
//    private KakaoUserClient kakaoUserClient;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private KakaoService kakaoService;
//
//    @Mock
//    private KakaoUserInfoDto kakaoUserInfo;
//
//
//    @BeforeEach
//    public void createKakaoUser() {
//        // KakaoUserInfoDto 응답 (사용자 정보)
//        // 1. Properties 객체 생성 후 닉네임 설정
//        KakaoUserInfoDto.Properties properties = new KakaoUserInfoDto.Properties();
//        properties.setNickname("TestUser");
//
//        // 2. KakaoAccount 객체 생성 후 이메일 설정
//        KakaoUserInfoDto.KakaoAccount kakaoAccount = new KakaoUserInfoDto.KakaoAccount();
//        kakaoAccount.setEmail("test@example.com");
//
//        // 3. 전체 DTO 생성
//        kakaoUserInfo = new KakaoUserInfoDto(3922677090L, properties, kakaoAccount);
//    }
//
//
//    @Test
//    public void testKakaoLogin_NewUser() throws JsonProcessingException {
//        // Given
//        String code = "sampleAuthCode";
//        String fakeAccessToken = "fakeAccessToken";
//
//        // KakaoTokenResponse 생성
//        KakaoTokenResponse tokenResponse = new KakaoTokenResponse();
//        ReflectionTestUtils.setField(tokenResponse, "accessToken", fakeAccessToken);
//
//        when(kakaoClient.getToken(eq("authorization_code"), anyString(), anyString(), eq(code)))
//                .thenReturn(tokenResponse);
//
//
//        when(kakaoUserClient.getKakaoUserInfo("Bearer " + fakeAccessToken))
//                .thenReturn(kakaoUserInfo);
//
//        // 신규 사용자인 경우, userRepository.findById()는 Optional.empty() 반환
//        when(userRepository.findById(String.valueOf(kakaoUserInfo.getId())))
//                .thenReturn(Optional.empty());
//
//        // 신규 사용자 저장 시 반환할 User 객체
//        User expectedUser = User.builder()
//                .Id(String.valueOf(kakaoUserInfo.getId()) + "kakao")
//                .nickname(kakaoUserInfo.getNickname() + "kakao")
//                .email(kakaoUserInfo.getEmail())
//                .password("kakao1234")
//                .phone("010-1111-1111")
//                .role(UserRoleEnum.USER)
//                .build();
//        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
//
//        // JWT 토큰 생성 시 반환할 값
//        String fakeJwtToken = "fakeJwtToken";
//        when(jwtUtil.createToken(expectedUser.getId(), expectedUser.getRole())).thenReturn(fakeJwtToken);
//
//        // When
//        String result = kakaoService.kakaoLogin(code);
//
//        // Then
//        assertEquals(fakeJwtToken, result);
//        verify(kakaoClient, times(1)).getToken(eq("authorization_code"), anyString(), anyString(), eq(code));
//        verify(kakaoUserClient, times(1)).getKakaoUserInfo("Bearer " + fakeAccessToken);
//        verify(userRepository, times(1)).findById(String.valueOf(kakaoUserInfo.getId()));
//        verify(userRepository, times(1)).save(any(User.class));
//        verify(jwtUtil, times(1)).createToken(expectedUser.getId(), expectedUser.getRole());
//    }
//
//    // 추가 테스트: 기존 회원이 존재할 경우
//    @Test
//    public void testKakaoLogin_ExistingUser() throws JsonProcessingException {
//        String code = "sampleAuthCode";
//        String fakeAccessToken = "fakeAccessToken";
//
//        KakaoTokenResponse tokenResponse = new KakaoTokenResponse();
//        ReflectionTestUtils.setField(tokenResponse, "accessToken", fakeAccessToken);
//
//        when(kakaoClient.getToken(eq("authorization_code"), anyString(), anyString(), eq(code)))
//                .thenReturn(tokenResponse);
//
//        when(kakaoUserClient.getKakaoUserInfo("Bearer " + fakeAccessToken))
//                .thenReturn(kakaoUserInfo);
//
//        // 기존 회원이 있는 경우
//        User existingUser = User.builder()
//                .Id(String.valueOf(kakaoUserInfo.getId()) + "kakao")
//                .nickname(kakaoUserInfo.getNickname() + "kakao")
//                .email(kakaoUserInfo.getEmail())
//                .password("kakao1234")
//                .phone("010-1111-1111")
//                .role(UserRoleEnum.USER)
//                .build();
//        when(userRepository.findById(String.valueOf(kakaoUserInfo.getId())))
//                .thenReturn(Optional.of(existingUser));
//
//        String fakeJwtToken = "fakeJwtToken";
//        when(jwtUtil.createToken(existingUser.getId(), existingUser.getRole())).thenReturn(fakeJwtToken);
//
//        String result = kakaoService.kakaoLogin(code);
//
//        assertEquals(fakeJwtToken, result);
//        verify(kakaoClient, times(1)).getToken(eq("authorization_code"), anyString(), anyString(), eq(code));
//        verify(kakaoUserClient, times(1)).getKakaoUserInfo("Bearer " + fakeAccessToken);
//        verify(userRepository, times(1)).findById(String.valueOf(kakaoUserInfo.getId()));
//        verify(userRepository, never()).save(any(User.class));  // save 호출 X
//        verify(jwtUtil, times(1)).createToken(existingUser.getId(), existingUser.getRole());
//    }
//}

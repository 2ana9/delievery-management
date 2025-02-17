package com.ana29.deliverymanagement.mvc;

import com.ana29.deliverymanagement.security.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.security.constant.jwt.JwtConfigEnum;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private final String secretKey = Base64.getEncoder().encodeToString("my-secret-key-for-testing".getBytes());
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
        // 주입할 secretKey를 설정 (application.properties 대신 테스트 환경에서 직접 설정)
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
        jwtUtil.init();
        // 예시: JwtConfigEnum에 필요한 값들을 테스트용으로 주입하거나, 상수값을 사용
        // 예를 들어, BEARER_PREFIX = "bearer " (소문자)와 BEARER_PREFIX_COUNT = "7", TOKEN_TIME = "3600000" (1시간)
    }

    @Test
    public void testCreateTokenAndParseClaims() {
        String username = "testUser";
        UserRoleEnum role = UserRoleEnum.USER;
        String token = jwtUtil.createToken(username, role);
        // 토큰이 BEARER 접두어를 포함한 상태로 생성된다면, getJwtFromHeader()는 접두어 제거를 수행하므로
        // createToken() 자체는 BEARER 접두어와 함께 반환하도록 설계되었다면, 테스트 시 접두어 제거 여부를 고려합니다.
        assertNotNull(token);
        // 테스트 목적: BEARER 접두어가 포함되어 있는지 확인
        assertTrue(token.startsWith(JwtConfigEnum.BEARER_PREFIX.getGetJwtConfig()));

        // 실제 토큰 부분 (접두어 제거)
        String pureToken = token.substring(Integer.parseInt(JwtConfigEnum.BEARER_PREFIX_COUNT.getGetJwtConfig()));
        Claims claims = jwtUtil.getUserInfoFromToken(pureToken);
        assertEquals(username, claims.getSubject());
        // 역할은 Claim으로 저장되어 있으므로 검증
        assertEquals(role, claims.get(JwtConfigEnum.AUTHORIZATION_HEADER.getGetJwtConfig()));
    }

    @Test
    public void testGetJwtFromHeader() {
        // Create a MockHttpServletRequest and set the header with a BEARER token
        MockHttpServletRequest request = new MockHttpServletRequest();
        String token = jwtUtil.createToken("testUser", UserRoleEnum.USER);
        request.addHeader(JwtConfigEnum.AUTHORIZATION_HEADER.getGetJwtConfig(), token);
        String extractedToken = jwtUtil.getJwtFromHeader(request);
        // 검증: extractedToken는 BEARER 접두어가 제거된 순수 토큰이어야 함
        assertFalse(extractedToken.startsWith(JwtConfigEnum.BEARER_PREFIX.getGetJwtConfig()));
    }

    @Test
    public void testValidateToken_Valid() {
        String token = jwtUtil.createToken("testUser", UserRoleEnum.USER);
        String pureToken = token.substring(Integer.parseInt(JwtConfigEnum.BEARER_PREFIX_COUNT.getGetJwtConfig()));
        assertTrue(jwtUtil.validateToken(pureToken));
    }

    @Test
    public void testValidateToken_Expired() throws InterruptedException {
        // 임시로 TOKEN_TIME를 짧게 설정해서 만료 테스트 진행
        // 예시로 현재 시간 + 1초로 만료되도록 변경 후 테스트, 테스트 후 원래 값 복원
        String originalTokenTime = JwtConfigEnum.TOKEN_TIME.getGetJwtConfig();
        ReflectionTestUtils.setField(jwtUtil, "TOKEN_TIME", "1000"); // 1초
        String token = jwtUtil.createToken("testUser", UserRoleEnum.USER);
        String pureToken = token.substring(Integer.parseInt(JwtConfigEnum.BEARER_PREFIX_COUNT.getGetJwtConfig()));
        // 2초 대기해서 토큰 만료
        Thread.sleep(2000);
        assertFalse(jwtUtil.validateToken(pureToken));
        // 복원
        ReflectionTestUtils.setField(jwtUtil, "TOKEN_TIME", originalTokenTime);
    }
}

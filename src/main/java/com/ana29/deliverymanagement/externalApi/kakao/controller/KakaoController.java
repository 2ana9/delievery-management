package com.ana29.deliverymanagement.externalApi.kakao.controller;

import com.ana29.deliverymanagement.externalApi.kakao.service.KakaoService;
import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.security.constant.jwt.JwtConfigEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users/kakao")
public class KakaoController {

    private final KakaoService kakaoService;

//    http://localhost:8080/api/users/kakao/authorize 로만 접속
    @GetMapping("/authorize")
    public String getKakaoAuthorizationUrl() {
        // 직접 리다이렉트 URL 생성 (또는 FeignClient의 getAuthorizationUrl() 사용 가능)
        String url = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoService.getKakaoClientId() +
                "&redirect_uri=" + kakaoService.getKakaoRedirectUri() +
                "&response_type=code";
        return "redirect:" + url;
    }

    @GetMapping("/callback")
    @ResponseBody
    public ResponseEntity<ResponseDto<Cookie>> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("kakaoLogin Controller Method, code: {}", code);
        // 서비스가 Bearer 접두어 없이 토큰을 반환하므로 substring 작업이 필요 없음.
        String token = kakaoService.kakaoLogin(code);
        log.info("kakao token: {}", token);

        Cookie cookie = new Cookie(JwtConfigEnum.AUTHORIZATION_HEADER.getGetJwtConfig(), token);
        log.info("kakao cookie: {}", cookie);
        cookie.setPath("/");

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDto.success(HttpStatus.OK, cookie));
    }
}



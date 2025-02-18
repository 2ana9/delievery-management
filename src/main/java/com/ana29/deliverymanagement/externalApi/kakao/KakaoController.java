package com.ana29.deliverymanagement.externalApi.kakao;

import com.ana29.deliverymanagement.security.constant.jwt.JwtConfigEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class KakaoController {
    private final KakaoService kakaoService;

    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("kakaoLogin Controller Method");
        String token = kakaoService.kakaoLogin(code).substring(Integer.parseInt(JwtConfigEnum.BEARER_PREFIX_COUNT.getGetJwtConfig()));
        log.info("kakao token : " + token);
        Cookie cookie = new Cookie(JwtConfigEnum.AUTHORIZATION_HEADER.getGetJwtConfig(), token);
        log.info("kakao cookie : " + cookie);

        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/api/users/sign-in";
    }

}

package com.ana29.deliverymanagement.externalApi.kakao.feign;

import com.ana29.deliverymanagement.externalApi.kakao.dto.KakaoTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 🔹 카카오 API 연동을 위한 FeignClient 정의
@FeignClient(name = "KakaoClient",
        url = "https://kauth.kakao.com")
public interface KakaoClient {

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoTokenResponseDto getToken(@RequestParam("grant_type") String grantType,
                                   @RequestParam("client_id") String clientId,
                                   @RequestParam("redirect_uri") String redirectUri,
                                   @RequestParam("code") String code);
}


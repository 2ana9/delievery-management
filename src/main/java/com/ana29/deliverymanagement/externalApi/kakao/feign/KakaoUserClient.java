package com.ana29.deliverymanagement.externalApi.kakao.feign;

import com.ana29.deliverymanagement.externalApi.kakao.dto.KakaoUserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// 🔹 카카오 사용자 정보를 가져오는 FeignClient
@FeignClient(name = "KakaoUserClient", url = "https://kapi.kakao.com")
public interface KakaoUserClient {

    // 🔹 2. 카카오 사용자 정보 요청
    @GetMapping(value = "/v2/user/me")
    KakaoUserInfoDto getKakaoUserInfo(@RequestHeader("Authorization") String accessToken);
}

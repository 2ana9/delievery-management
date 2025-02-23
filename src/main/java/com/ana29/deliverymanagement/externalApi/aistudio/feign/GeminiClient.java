package com.ana29.deliverymanagement.externalApi.aistudio.feign;

import com.ana29.deliverymanagement.externalApi.aistudio.dto.GeminiRequestDto;
import com.ana29.deliverymanagement.externalApi.aistudio.dto.GeminiResponseDto;
import com.ana29.deliverymanagement.externalApi.aistudio.config.GeminiClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 🔹 Gemini API 연동을 위한 FeignClient 정의
@FeignClient(name = "GeminiClient",
        url = "https://generativelanguage.googleapis.com",
        configuration = GeminiClientConfig.class)
public interface GeminiClient {

    /**
     * Gemini 모델을 호출하여 콘텐츠를 생성합니다.
     *
     * @param request 요청 본문 (JSON 형식)
     * @return 생성된 콘텐츠에 대한 응답 DTO
     */
    @PostMapping(value = "/v1beta/models/gemini-1.5-flash:generateContent",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    GeminiResponseDto generateContent(@RequestBody GeminiRequestDto request);
}

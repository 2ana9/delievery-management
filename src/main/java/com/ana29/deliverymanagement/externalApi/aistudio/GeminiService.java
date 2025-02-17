package com.ana29.deliverymanagement.externalApi.aistudio;

import com.ana29.deliverymanagement.externalApi.aistudio.feign.GeminiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j(topic = "Gemini Service")
@RequiredArgsConstructor
@Service
public class GeminiService {

    private final GeminiClient geminiClient;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String generateContent(String prompt) {
        // 요청 텍스트에 "답변을 최대한 간결하게 50자 이하로" 문구 추가
        String finalPrompt = prompt + SendToAiMessage.ADDITIONAL_MESSAGE.getSendToAiMessage();
        GeminiRequestDto request = new GeminiRequestDto(
                List.of(new GeminiRequestDto.Content(
                        List.of(new GeminiRequestDto.Part(finalPrompt))
                ))
        );
        GeminiResponseDto response = geminiClient.generateContent(geminiApiKey, request);
        // 예시: 첫 번째 생성된 콘텐츠의 텍스트 반환
        if(response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()){
            return response.getCandidates().get(0).getContent().getParts().get(0).getText();
        }
        return null;
    }
}

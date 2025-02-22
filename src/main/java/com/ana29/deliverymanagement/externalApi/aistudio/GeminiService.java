package com.ana29.deliverymanagement.externalApi.aistudio;

import com.ana29.deliverymanagement.externalApi.aistudio.dto.GeminiRequestDto;
import com.ana29.deliverymanagement.externalApi.aistudio.dto.GeminiResponseDto;
import com.ana29.deliverymanagement.externalApi.aistudio.entity.Gemini;
import com.ana29.deliverymanagement.externalApi.aistudio.feign.GeminiClient;
import com.ana29.deliverymanagement.externalApi.aistudio.repository.GeminiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j(topic = "Gemini Service")
@RequiredArgsConstructor
@Service
public class GeminiService {

    private final GeminiClient geminiClient;
    private final GeminiRepository geminiRepository;


    @Transactional
    public void generateContent(GeminiRequestDto requestDto) {
        // requestDto에서 첫 번째 Content의 첫 번째 Part의 텍스트를 원본 프롬프트로 사용 (예시)
        String originalPrompt = requestDto.getContents()
                .get(0)
                .getParts()
                .get(0)
                .getText();
        // 요청 텍스트에 추가 문구를 붙여 최종 프롬프트 생성
        String finalPrompt = originalPrompt + SendToAiMessage.ADDITIONAL_MESSAGE.getSendToAiMessage();

        // 최종 프롬프트를 포함한 GeminiRequestDto 생성
        GeminiRequestDto modifiedRequest = new GeminiRequestDto(
                List.of(new GeminiRequestDto.Content(
                        List.of(new GeminiRequestDto.Part(finalPrompt))
                ))
        );

        // Gemini 클라이언트를 통해 콘텐츠 생성 요청
        GeminiResponseDto response = geminiClient.generateContent(modifiedRequest);

        // 응답에서 첫 번째 후보의 텍스트를 최종 답변으로 추출
        if(response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()){
            String finalResponse = response.getCandidates()
                    .get(0)
                    .getContent()
                    .getParts()
                    .get(0)
                    .getText();

            // 질문(finalPrompt)과 답변(finalResponse)을 함께 저장
            Gemini gemini = Gemini.builder()
                    .question(finalPrompt)
                    .answer(finalResponse)
                    .build();
            saveGeneratedContent(gemini);
        }
    }

    private void saveGeneratedContent(Gemini gemini) {
        geminiRepository.save(gemini);
    }


}

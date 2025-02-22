package com.ana29.deliverymanagement.externalApi.aistudio.controller;

import com.ana29.deliverymanagement.externalApi.aistudio.GeminiService;
import com.ana29.deliverymanagement.externalApi.aistudio.dto.GeminiRequestDto;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    @PostMapping("/question")
    public void geminiGetAnswer(@RequestBody @Valid GeminiRequestDto requestDto){
        geminiService.generateContent(requestDto);
    }
//    ex)
//{
//    "contents": [
//    {
//        "parts": [
//        {
//            "text": "김치찌개 메뉴를 팔거야. 메뉴에 대한 설명을 적어줘."
//        }
//      ]
//    }
//  ]
//}

    @DeleteMapping("/{id}")
    public void deleteGemini(@PathVariable UUID id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        geminiService.softDeleteGemini(id, userDetails);
    }

}

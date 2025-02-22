package com.ana29.deliverymanagement.externalApi.aistudio.controller;

import com.ana29.deliverymanagement.externalApi.aistudio.GeminiService;
import com.ana29.deliverymanagement.externalApi.aistudio.dto.GeminiRequestDto;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class GeminiController {
    private final GeminiService geminiService;

    @PostMapping("/gemini_service")
    public void geminiGetAnswer(@RequestBody @Valid GeminiRequestDto requestDto){
        geminiService.generateContent(requestDto);
    }
//    ex) http://localhost:8080/api/ai/gemini_service?prompt=맛있는음식10개

}

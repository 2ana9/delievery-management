package com.ana29.deliverymanagement.externalApi.aistudio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class GeminiController {
    private final GeminiService geminiService;

    @PostMapping("/gemini_service")
    @ResponseBody
    public String geminiGetAnswer(){
        return geminiService.generateContent("명언 5개만 말해봐.");
    }

}

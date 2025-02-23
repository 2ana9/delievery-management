package com.ana29.deliverymanagement.externalApi.aistudio.dto;

import com.ana29.deliverymanagement.externalApi.aistudio.entity.Gemini;

public record CreatedGeminiResponseDto (String request, String response) {

	public static CreatedGeminiResponseDto from(Gemini gemini) {
		return new CreatedGeminiResponseDto(gemini.getQuestion(), gemini.getAnswer());
	}
}

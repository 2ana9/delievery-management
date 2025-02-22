package com.ana29.deliverymanagement.externalApi.aistudio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDto {

    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;
    private String modelVersion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Double avgLogprobs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
        private String role;  // 예: "model"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageMetadata {
        private int promptTokenCount;
        private int candidatesTokenCount;
        private int totalTokenCount;
        private List<TokenDetails> promptTokensDetails;
        private List<TokenDetails> candidatesTokensDetails;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenDetails {
        private String modality;
        private int tokenCount;
    }
}

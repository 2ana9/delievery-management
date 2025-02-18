package com.ana29.deliverymanagement.externalApi.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class KakaoTokenResponseDto {
    @JsonProperty("access_token")
    private String accessToken;
}

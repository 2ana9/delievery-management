package com.ana29.deliverymanagement.externalApi.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfoDto {
    private Long id;

    @JsonProperty("properties")
    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public String getNickname() {
        return properties != null ? properties.getNickname() : null;
    }

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Properties {
        private String nickname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
    }
}

package com.ana29.deliverymanagement.security.config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class AuthorityDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        // 만약 현재 토큰이 배열 시작이면
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            // 배열의 첫 번째 요소로 이동
            jp.nextToken();
            String value = jp.getText();
            // 배열의 나머지 토큰을 모두 스킵 (배열 종료 토큰까지)
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                // 아무 작업도 하지 않음
            }
            return value;
        }
        // 배열이 아닌 경우에는 그대로 문자열 반환
        return jp.getValueAsString();
    }
}

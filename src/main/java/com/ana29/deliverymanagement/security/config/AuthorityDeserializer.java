package com.ana29.deliverymanagement.security.config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class AuthorityDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        // JSON 트리 형태로 읽기
        JsonNode node = jp.getCodec().readTree(jp);

        // 1. 만약 노드가 객체라면 "authority" 필드가 있는지 확인
        if (node.isObject() && node.has("authority")) {
            return node.get("authority").asText();
        }

        // 2. 만약 노드가 배열이라면, 배열의 첫 요소나 두번째 요소에서 "authority" 필드를 찾습니다.
        if (node.isArray()) {
            // 배열 내부의 각 요소를 순회하여 authority 필드가 있는 객체를 찾습니다.
            for (JsonNode element : node) {
                if (element.isObject() && element.has("authority")) {
                    return element.get("authority").asText();
                }
            }
        }

        // 3. 만약 노드가 단순 텍스트라면 그대로 반환
        if (node.isTextual()) {
            return node.asText();
        }

        throw new IOException("Unable to deserialize authority from node: " + node.toString());
    }
}
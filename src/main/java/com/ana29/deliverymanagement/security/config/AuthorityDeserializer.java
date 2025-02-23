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

        // 예상 구조: [ "org.springframework.security.core.authority.SimpleGrantedAuthority", {"authority": "MASTER"} ]
        if (node.isArray() && node.size() == 2) {
            JsonNode secondElement = node.get(1);
            if (secondElement.isObject() && secondElement.has("authority")) {
                return secondElement.get("authority").asText();
            }
        }
        throw new IOException("Unable to deserialize authority from node: " + node.toString());
    }
}

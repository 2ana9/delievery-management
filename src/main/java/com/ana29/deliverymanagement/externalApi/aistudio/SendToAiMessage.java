package com.ana29.deliverymanagement.externalApi.aistudio;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SendToAiMessage {

    HEADER("{\"contents\": [{\"parts\": [{\"text\": \""),
    ADDITIONAL_MESSAGE(", 답변을 최대한 간결하게 50자 이하로."),
    FOOTER("\"}]}]}");

    private final String SendToAiMessage;

}

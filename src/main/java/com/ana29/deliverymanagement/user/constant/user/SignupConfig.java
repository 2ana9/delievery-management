package com.ana29.deliverymanagement.user.constant.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public enum SignupConfig {
    // 사용자명: 영문, 숫자 1~50자
    USERNAME_PATTERN(Pattern.compile("^[a-zA-Z0-9]{1,50}$")),
    // 닉네임: 한글, 영문, 숫자 2~20자
    NICKNAME_PATTERN(Pattern.compile("^[가-힣a-zA-Z0-9]{2,20}$")),
    // 전화번호: 010-XXXX-XXXX 또는 010XXXXXXXX
    PHONE_PATTERN(Pattern.compile("^010-?\\d{4}-?\\d{4}$")),
    // 이메일: 일반적인 이메일 형식
    EMAIL_PATTERN(Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,10}$"));

    private final Pattern pattern;

}

package com.ana29.deliverymanagement.config.admin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AdminConfig {
    @Value("${admin.signup.key}") // application.properties에서 값 주입
    private String adminSignupKey;
}
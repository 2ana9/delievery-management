package com.ana29.deliverymanagement.config.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminConfig {

    @Value("${admin.signup.key}") // application.properties에서 값 주입
    private String adminSignupKey;

    public String getAdminSignupKey() {
        return adminSignupKey;
    }
}
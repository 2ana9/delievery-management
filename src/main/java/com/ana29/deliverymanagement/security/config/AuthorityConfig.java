package com.ana29.deliverymanagement.security.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AuthorityConfig {
    @Value("${master.signup.key}") // application.properties에서 값 주입
    private String masterSignupKey;
    @Value("${manager.signup.key}")
    private String managerSignupKey;
    @Value("${owner.signup.key}")
    private String ownerSignupKey;
}
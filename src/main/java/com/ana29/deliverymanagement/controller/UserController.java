package com.ana29.deliverymanagement.controller;


import com.ana29.deliverymanagement.config.jwt.TokenBlacklist;
import com.ana29.deliverymanagement.dto.SignupRequestDto;
import com.ana29.deliverymanagement.service.Userservice;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final Userservice userService;
    private String ifSuccessRedirectUrl;
    @GetMapping("/sign-up")
    public String signUpPage(){
        log.info("connet Test : /sign-up (Get Method)");
//        타임리프 의존성이 없으면 템플릿 파일 밑의 signup.html 을 찾지 못해 403에러 발생함
        return "signup";
    }
    @PostMapping("/sign-up")
    public String signUp(@RequestBody @Valid SignupRequestDto requestDto, BindingResult bindingResult){
        log.info("connet Test : /sign-up (Post Method)");
        ifSuccessRedirectUrl = userService.signup(requestDto, bindingResult);
        log.info("Method Complete Test : /sign-up (Post Method)");
        return "redirect:" + ifSuccessRedirectUrl;
    }

    @GetMapping("/sign-in")
    public String loginPage(){
        return "login";
    }
    @PostMapping("/sign-in")
    public String signIn(){
        return "login";
    }

    @PostMapping("/sign-out")
    public String signOut(HttpServletRequest request){
        log.info("connet Test : /sign-out (Post Method)");
        ifSuccessRedirectUrl = userService.signOut(request);
        log.info("TOKEN BLACKLIST VALUE : " + TokenBlacklist.getBlacklistedTokens().toString());
        return "redirect:" + ifSuccessRedirectUrl;
    }

    @GetMapping("/me")
    public void findUser(){

    }

    @PatchMapping("/me")
    public void modifyUser(){

    }

    @DeleteMapping("/me")
    public void deleteUser(){

    }

}

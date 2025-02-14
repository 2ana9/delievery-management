package com.ana29.deliverymanagement.controller;


import com.ana29.deliverymanagement.dto.SignupRequestDto;
import com.ana29.deliverymanagement.service.Userservice;
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

    @PostMapping("/sign-up")
    public String signUp(@Valid SignupRequestDto requestDto, BindingResult bindingResult){
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/users/sign-up";
        }

        userService.signup(requestDto);

        return "redirect:/api/users/sign-in";
    }

    @GetMapping("/sign-in")
    public String loginPage(){
        return "login";
    }
    @PostMapping("/sign-in")
    public void signIn(){

    }
    @PostMapping("/sign-out")
    public void signOut(){

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

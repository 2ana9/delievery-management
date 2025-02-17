package com.ana29.deliverymanagement.user.controller;


import com.ana29.deliverymanagement.global.config.jwt.TokenBlacklist;
import com.ana29.deliverymanagement.global.constant.jwt.JwtConfigEnum;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
import com.ana29.deliverymanagement.user.dto.UserInfoDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.service.KakaoService;
import com.ana29.deliverymanagement.user.service.Userservice;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final Userservice userService;
    private final KakaoService kakaoService;
    private String ifSuccessRedirectUrl;

    @GetMapping("/sign-up")
    public String signUpPage(){
        log.info("connet Test : /sign-up (Get Method)");
//        타임리프 의존성이 없으면 템플릿 파일 밑의 signup.html 을 찾지 못해 403에러 발생함
        return "signup";
    }
    @PostMapping("/sign-up")
    public String signUp(@RequestBody @Valid SignupRequestDto requestDto){
        log.info("connet Test : /sign-up (Post Method)");
//        /api/users/sign-in
        ifSuccessRedirectUrl = userService.signup(requestDto);
        log.info("Method Complete Test : /sign-up (Post Method)");
        return "redirect:" + ifSuccessRedirectUrl;
    }
//    유저가 로그인을 하면 로그인하기 전 페이지로 돌아가도록 하고 싶다.
//    생각해보면 크게 두 가지 경우가 있을수 있다.
//
//    유저가 직접 로그인 버튼을 클릭해서 로그인폼으로 이동 후 로그인 성공
//    유저가 권한이 없는 경로에 접근해서 스프링 시큐리티가 인터셉트 한 후에 로그인 페이지 요청으로 바꿔 서블릿에 전달
//    @GetMapping("/sign-in")
//    @ResponseBody
//    public String signInPage(Model model, HttpServletRequest request){
//        model.addAttribute("menu", "login");
//        String prevPage = request.getHeader("Referer");
//        log.info("loginForm prevPage = {}", prevPage);
//        if(prevPage != null && !prevPage.contains("/login")) {
//            request.getSession().setAttribute("prevPage", prevPage);
//        }
//        return "login test";
//    }

    @GetMapping("/sign-in")
    public String signInPage(){
        return "login";
    }
    @PostMapping("/sign-in")
    public String signIn(){
        return "login";
    }

    @PostMapping("/sign-out")
    public String signOut(HttpServletRequest request){
        log.info("connet Test : /sign-out (Post Method)");
//        /api/users/sign-in
        ifSuccessRedirectUrl = userService.signOut(request);
        log.info("TOKEN BLACKLIST VALUE : " + TokenBlacklist.getBlacklistedTokens().toString());
        return "redirect:" + ifSuccessRedirectUrl;
    }

    @PostMapping("/me")
    @ResponseBody
    public List<UserInfoDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserInfo(userDetails);
    }

    @PatchMapping("/me")
    @ResponseBody
    public List<UserInfoDto> modifyUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody @Valid UpdateRequestDto updateDto){
        return userService.modifyUserInfo(userDetails, updateDto);
    }

    @DeleteMapping("/me")
    public String deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                           @RequestBody @Valid UpdateRequestDto updateDto){
        userService.deleteUser(userDetails, updateDto);
        return "redirect:/api/users/sign-in";

    }

    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("kakaoLogin Controller Method");
        String token = kakaoService.kakaoLogin(code).substring(Integer.parseInt(JwtConfigEnum.BEARER_PREFIX_COUNT.getGetJwtConfig()));
        log.info("kakao token : " + token);
        Cookie cookie = new Cookie(JwtConfigEnum.AUTHORIZATION_HEADER.getGetJwtConfig(), token);
        log.info("kakao cookie : " + cookie);

        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/api/users/sign-in";
    }

}

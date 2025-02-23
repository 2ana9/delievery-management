package com.ana29.deliverymanagement.user.controller;


import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
import com.ana29.deliverymanagement.user.dto.UserInfoDto;
import com.ana29.deliverymanagement.user.service.Userservice;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	private String ifSuccessRedirectUrl;

	@GetMapping("/sign-up")
	public String signUpPage() {
//        타임리프 의존성이 없으면 템플릿 파일 밑의 signup.html 을 찾지 못해 403에러 발생
		return "signup";
	}

	@PostMapping("/sign-up")
	public String signUp(@RequestBody @Valid SignupRequestDto requestDto) {
//        /api/users/sign-in
		ifSuccessRedirectUrl = userService.signup(requestDto);
		return "redirect:" + ifSuccessRedirectUrl;
	}

	@GetMapping("/sign-in")
	public String signInPage() {
		return "login";
	}

	@PostMapping("/sign-in")
	public String signIn() {
		return "login";
	}

	@PostMapping("/sign-out")
	public String signOut(@AuthenticationPrincipal UserDetailsImpl userDetails,
		HttpServletRequest request) {
//        /api/users/sign-in
		ifSuccessRedirectUrl = userService.signOut(userDetails, request);
		return "redirect:" + ifSuccessRedirectUrl;
	}

	@GetMapping("/me")
	@ResponseBody
	public ResponseEntity<ResponseDto<List<UserInfoDto>>> getUserInfo(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
		@RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {

		List<UserInfoDto> response = userService.getUserInfo(userDetails, page, size, sortBy, isAsc);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ResponseDto.success(HttpStatus.OK, response));
	}
	// ex) GET 요청, /api/users/me?page=2&size=30&sortBy=updatedAt&isAsc=false

	@PatchMapping("/me")
	@ResponseBody
	public ResponseEntity<ResponseDto<UserInfoDto>> modifyUserInfo(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody @Valid UpdateRequestDto updateDto) {

		UserInfoDto response = userService.modifyUserInfo(userDetails, updateDto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ResponseDto.success(HttpStatus.OK, response));
	}

	@DeleteMapping("/me")
	public String deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		userService.deleteUser(userDetails);
		return "redirect:/api/users/sign-in";
	}

}

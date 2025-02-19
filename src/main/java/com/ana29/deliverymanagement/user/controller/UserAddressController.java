package com.ana29.deliverymanagement.user.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.dto.CreateUserAddressRequestDto;
import com.ana29.deliverymanagement.user.dto.CreateUserAddressResponseDto;
import com.ana29.deliverymanagement.user.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity<ResponseDto<CreateUserAddressResponseDto>> createUserAddress(
            @RequestBody @Valid CreateUserAddressRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CreateUserAddressResponseDto response =
                userAddressService.createUserAddress(requestDto, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto<>(HttpStatus.CREATED, response));
    }
}

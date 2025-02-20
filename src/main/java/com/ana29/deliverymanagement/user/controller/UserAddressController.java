package com.ana29.deliverymanagement.user.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.user.dto.*;
import com.ana29.deliverymanagement.user.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<Page<GetUserAddressesResponseDto>>> getUserAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @ModelAttribute GetUserAddressesRequestDto condition, Pageable pageable) {

        Page<GetUserAddressesResponseDto> response =
                userAddressService.getUserAddresses(condition, pageable, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<UpdateUserAddressResponseDto>> updateUserAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserAddressRequestDto requestDto) {

        UpdateUserAddressResponseDto response =
                userAddressService.updateUserAddresses(id, requestDto, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ResponseDto<UpdateUserAddressResponseDto>> setDefaultAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id) {

        UpdateUserAddressResponseDto response =
                userAddressService.setDefaultAddress(id, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<DeleteUserAddressResponseDto>> deleteUserAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id) {

        DeleteUserAddressResponseDto response =
                userAddressService.deleteUserAddresses(id, userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    }
}

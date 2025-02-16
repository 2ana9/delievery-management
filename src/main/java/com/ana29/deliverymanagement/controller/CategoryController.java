package com.ana29.deliverymanagement.controller;

import com.ana29.deliverymanagement.constant.UserRoleEnum;
import com.ana29.deliverymanagement.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.entity.User;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    //음식 카테고리 추가
    @PostMapping("/categories")
    public CategoryResponseDto createCategory(@RequestBody CategoryRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.ADMIN) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
        return categoryService.createCategory(requestDto);
    };

    //음식 카테고리 수정
    @PutMapping("/categories/{id}") //카테고리 이름수정
    public CategoryResponseDto updateCategory(@PathVariable UUID id,
                                              @RequestBody CategoryRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.ADMIN) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
        return categoryService.updateCategory(id,requestDto);
    };

    //음식 카테고리 조회


    //음식 카테고리 삭제



}

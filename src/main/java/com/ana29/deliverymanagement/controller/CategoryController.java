package com.ana29.deliverymanagement.controller;

import com.ana29.deliverymanagement.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public CategoryResponseDto createCategory(@RequestBody CategoryRequestDto requestDto){
        return categoryService.createCategory(requestDto);
    }

//    @PostMapping("/categories")
//    public CategoryResponseDto createCategory(@RequestBody CategoryRequestDto requestDto, @AuthenticationPrincipal User user) throws AccessDeniedException {
//        UserRoleEnum userRole = user.getRole();
//        if (userRole != UserRoleEnum.ADMIN) {
//            throw new AccessDeniedException("관리자 접근이 필요합니다.");
//        }
//        return categoryService.createCategory(requestDto);
//    }


}

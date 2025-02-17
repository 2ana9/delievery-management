package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.security.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.restaurant.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@RequestBody CategoryRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);
        return categoryService.createCategory(requestDto);
    };

    //음식 카테고리 수정
    @PutMapping("/categories/{id}") //카테고리 이름수정
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponseDto updateCategory(@PathVariable UUID id,
                                              @RequestBody CategoryRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException {
        checkUserAccess(userDetails);
        return categoryService.updateCategory(id,requestDto);
    };

    //음식 카테고리 조회
    @GetMapping("/api/categories?page={page}&size={size}&sortBy={sortBy}&isAsc={isAsc}")
    public Page<CategoryResponseDto> getAllCategories(@RequestParam("page") int page,
                                                        @RequestParam("size") int size,
                                                        @RequestParam("sortBy") String sortBy,
                                                        @RequestParam("isAsc") boolean isAsc) {
        return categoryService.getAllCategories(
                page-1,size,sortBy,isAsc);
    };


    //음식 카테고리 삭제
    @DeleteMapping("categories/{id}")
    public CategoryResponseDto deleteCategory(@PathVariable UUID id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);
        return categoryService.deleteCategory(id);
    };
    
    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.ADMIN) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };



}

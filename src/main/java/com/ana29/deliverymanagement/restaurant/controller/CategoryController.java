package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.user.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.restaurant.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseDto<CategoryResponseDto>> createCategory(@RequestBody @Valid CategoryRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);

        CategoryResponseDto response =
                categoryService.createCategory(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto<>(HttpStatus.CREATED, response));
    };

    //음식 카테고리 수정
    @PutMapping("/categories/{id}") //카테고리 이름수정
    public ResponseEntity<ResponseDto<CategoryResponseDto>> updateCategory(@PathVariable UUID id,
                                              @RequestBody CategoryRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException {
        checkUserAccess(userDetails);
        CategoryResponseDto response = categoryService.updateCategory(id,requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    };

//    //음식 카테고리 조회
//    @GetMapping("/api/categories?page={page}&size={size}&sortBy={sortBy}&isAsc={isAsc}")
//    public Page<CategoryResponseDto> getAllCategories(@RequestParam("page") int page,
//                                                        @RequestParam("size") int size,
//                                                        @RequestParam("sortBy") String sortBy,
//                                                        @RequestParam("isAsc") boolean isAsc) {
//        return categoryService.getAllCategories(
//                page-1,size,sortBy,isAsc);
//    };


    //음식 카테고리 삭제
    @DeleteMapping("categories/{id}")
    public ResponseEntity<ResponseDto<CategoryResponseDto>> deleteCategory(@PathVariable UUID id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);
        CategoryResponseDto response = categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto<>(HttpStatus.OK, response));
    };
    
    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.MASTER) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };



}

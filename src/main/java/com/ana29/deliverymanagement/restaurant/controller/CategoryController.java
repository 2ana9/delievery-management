package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.user.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.restaurant.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    //음식 카테고리 추가
    @PostMapping
    public ResponseDto<Category> createCategory(@RequestBody @Valid CategoryRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);

        return categoryService.createCategory(requestDto);
    };

    //음식 카테고리 수정
    @PutMapping("/{id}") //카테고리 이름수정
    public ResponseDto<Category> updateCategory(@PathVariable UUID id,
                                              @RequestBody CategoryRequestDto requestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails)throws AccessDeniedException {
        checkUserAccess(userDetails);

        return categoryService.updateCategory(id,requestDto,userDetails.getUser().getId());
    };

    //음식 카테고리 조회(전체)
    @GetMapping
    public ResponseDto<Page<Category>> getAllCategories(Pageable pageable) {

        return categoryService.getAllCategories(pageable);
    };

    //search (삭제처리된 카테고리의경우 조회안되게 숨김처리)
    @GetMapping("/search")
    public ResponseDto<List<Category>> searchCategories(
            @PathVariable(required = false) UUID id,
            @RequestParam(required = false) String foodType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Sort sortValue = checkSortValue(sort); //정렬기준 체크

        if(size != 10 && size != 30 && size != 50){//10건 30건 50건 외에는 10건 고정
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size, sortValue);

        return categoryService.searchCategories(id,foodType,pageable);
    };

    //음식 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseDto<Category> deleteCategory(@PathVariable UUID id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {
        checkUserAccess(userDetails);

        return categoryService.deleteCategory(id,userDetails.getUser().getId());
    };
    
    //사용자의 권한확인 메소드
    public void checkUserAccess(UserDetailsImpl userDetails) throws AccessDeniedException {
        UserRoleEnum userRole = userDetails.getUser().getRole();
        if (userRole != UserRoleEnum.MASTER) {
            throw new AccessDeniedException("관리자 접근이 필요합니다.");
        }
    };

    //생성일순,수정일순 정렬체크
    private Sort checkSortValue(String sort) {
        String[] sortParams = sort.split(",");
        String sortType = sortParams[0];//정렬기준
        String sortDirection = sortParams[1];//asc,desc

        if (sortDirection.equalsIgnoreCase("desc")) {
            return Sort.by(Sort.Order.desc(sortType));
        } else {
            return Sort.by(Sort.Order.asc(sortType));
        }
    }


}

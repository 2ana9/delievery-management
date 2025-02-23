package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.PaginationDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.repository.CategoryRepository;
import com.ana29.deliverymanagement.restaurant.repository.CategorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(CategoryRequestDto requestDto){
       Category category = categoryRepository.save(
               Category.builder()
                       .foodType(requestDto.getFoodType())
                       .build()
       );

        return category;
    }

    @Transactional
    public Category updateCategory(UUID id, CategoryRequestDto requestDto,String userId) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.update(requestDto);
        category.setUpdatedAt(LocalDateTime.now());
        category.setUpdatedBy(userId);
        categoryRepository.save(category);

        return category;
    }

    //음식 카테고리 전체조회
    @Transactional(readOnly = true)
    public ResponseDto<Page<Category>> getAllCategories(Pageable pageable) {
        Page<Category> category = categoryRepository.findByIsDeletedFalse(pageable);

        return ResponseDto.success(category);
    };

    //음식 카테고리 search
    @Transactional
    public ResponseDto<List<Object>> searchCategories(UUID id, String foodType, Pageable pageable) {
        Specification<Category> spec = Specification.where(CategorySpecification.hasId(id)
                .and(CategorySpecification.hasFoodType(foodType))
                .and(CategorySpecification.isNotDeleted()));
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);

        // 페이징 정보 생성
        PaginationDto pagination = new PaginationDto(
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.isFirst(),
                categoryPage.isLast(),
                categoryPage.getNumberOfElements(),
                categoryPage.isEmpty()
        );

        // Restaurant 리스트와 PaginationDto를 하나의 리스트에 담기
        List<Object> responseData = new ArrayList<>(categoryPage.getContent());
        responseData.add(pagination);

        return ResponseDto.success(responseData);
    }

    public ResponseDto<Category> deleteCategory(UUID id, String userId) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.setIsDeleted(true);
        category.setDeletedAt(LocalDateTime.now());
        category.setDeletedBy(userId);
        categoryRepository.save(category);

        return ResponseDto.success(category);
    };


}

package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public ResponseDto<Category> createCategory(CategoryRequestDto requestDto){
       Category category = categoryRepository.save(
               Category.builder()
                       .foodType(requestDto.getFoodType())
                       .build()
       );

        return ResponseDto.success(category);
    }

    @Transactional
    public ResponseDto<Category> updateCategory(UUID id, CategoryRequestDto requestDto,String userId) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.update(requestDto);
        category.setUpdatedAt(LocalDateTime.now());
        category.setDeletedBy(userId);
        categoryRepository.save(category);

        return ResponseDto.success(category);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<Category>> getAllCategories(Pageable pageable) {
        Page<Category> category = categoryRepository.findAll(pageable);

        return ResponseDto.success(category);
    };

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

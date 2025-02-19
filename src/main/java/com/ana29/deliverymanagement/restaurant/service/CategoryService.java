package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.entity.Restaurant;
import com.ana29.deliverymanagement.restaurant.repository.CategoryRepository;
import com.ana29.deliverymanagement.restaurant.repository.CategorySpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    //음식 카테고리 전체조회
    @Transactional(readOnly = true)
    public ResponseDto<Page<Category>> getAllCategories(Pageable pageable) {
        Page<Category> category = categoryRepository.findAll(pageable);

        return ResponseDto.success(category);
    };

    //음식 카테고리 id로 조회
    @Transactional
    public ResponseDto<List<Category>> searchCategories(UUID id, String foodType, Pageable pageable) {
        Specification<Category> spec = Specification.where(CategorySpecification.hasId(id)
                .and(CategorySpecification.hasFoodType(foodType)));
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);

        return ResponseDto.success(categoryPage.getContent());
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

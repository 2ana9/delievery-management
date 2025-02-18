package com.ana29.deliverymanagement.restaurant.service;

import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto){
       Category category = categoryRepository.save(
               Category.builder()
                       .foodType(requestDto.getFoodType())
                       .build()
       );

        return CategoryResponseDto.from(category);
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.update(requestDto);

        return CategoryResponseDto.from(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {

        return categoryRepository.findAll(pageable).map(CategoryResponseDto::from);
    };

    public CategoryResponseDto deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.setIsDeleted(true);
        categoryRepository.save(category);
        return CategoryResponseDto.from(category);
    };


}

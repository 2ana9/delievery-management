package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.entity.Category;
import com.ana29.deliverymanagement.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto){
       Category category = categoryRepository.save(new Category(requestDto));
       
        return new CategoryResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.update(requestDto);

        return new CategoryResponseDto(category);
    }

    public CategoryResponseDto deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.setIsDeleted(true);
        categoryRepository.save(category);
        return new CategoryResponseDto(category);
    };
}

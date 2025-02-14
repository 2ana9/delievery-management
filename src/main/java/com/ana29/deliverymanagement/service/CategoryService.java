package com.ana29.deliverymanagement.service;

import com.ana29.deliverymanagement.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.entity.Category;
import com.ana29.deliverymanagement.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Category category = categoryRepository.save(new Category(requestDto));
        return new CategoryResponseDto(category);
    }
}

package com.ana29.deliverymanagement.category.service;

import com.ana29.deliverymanagement.category.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.category.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.category.entity.Category;
import com.ana29.deliverymanagement.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(CategoryRequestDto requestDto){
       Category category = categoryRepository.save(
               Category.builder()
                       .foodType(requestDto.getFoodType())
                       .build()
       );
       
        return new CategoryResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.update(requestDto);

        return new CategoryResponseDto(category);
    }

    public Page<CategoryResponseDto> getAllCategories(int page, int size, String sortBy, boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC: Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy); //정렬방향,정렬기준
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Category> categoryList = categoryRepository.findAll(pageable);

        return categoryList.map(CategoryResponseDto::new);
    };

    public CategoryResponseDto deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("Category not found"));
        category.setIsDeleted(true);
        categoryRepository.save(category);
        return new CategoryResponseDto(category);
    };


}

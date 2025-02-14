package com.ana29.deliverymanagement.controller;

import com.ana29.deliverymanagement.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.dto.CategoryResponseDto;
import com.ana29.deliverymanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    @PostMapping("/categories")
    public void createCategory(@RequestBody CategoryRequestDto requestDto) {
        categoryService.createCategory(requestDto);
    }

}

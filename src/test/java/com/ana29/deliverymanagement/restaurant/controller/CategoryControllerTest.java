package com.ana29.deliverymanagement.restaurant.controller;

import com.ana29.deliverymanagement.restaurant.dto.CategoryRequestDto;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private CategoryRepository categoryRepository;

    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    //회원 로그인 token 쓰고있어서 로그인테스트를 먼저..?
    //로그인 완료된 토큰값을 받아와서 테스트 메소드 진행해보기(전체 테스트 메소드는 토큰값이 필요하다! 로그인을 해야한다

    @Test
    void testCreateCategory() throws Exception{
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setFoodType("간식");
        Category category = categoryRepository.save(
                Category.builder()
                        .foodType(requestDto.getFoodType())
                        .build()
        );//이렇게해야 저장이되

        //MockMVc
    }

    @Test
    void testUpdateCategory() throws Exception{//만들었던거에서 아이디를 받아와서 해야하나?

    }

    @Test
    void testGetAllCategories() throws Exception{//전체조회

    }

    @Test
    void testSearchCategories() throws Exception{//필터링 서치

    }

    @Test
    void testDeleteCategory() throws Exception{//삭제할때도 만들었던거에서 아이디를 받아와서 해야할것같다 맞나..?

    }
}
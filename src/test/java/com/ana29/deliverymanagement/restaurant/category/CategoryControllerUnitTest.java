package com.ana29.deliverymanagement.restaurant.category;

import com.ana29.deliverymanagement.restaurant.controller.CategoryController;
import com.ana29.deliverymanagement.restaurant.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerUnitTest {
    //단위테스트 (삭제,검색)

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void testGetCategory() throws Exception {

    }

    @Test
    void testSearchCategory() throws Exception {

    }

    @Test
    void testCategoryDelete() throws Exception {

    }
}

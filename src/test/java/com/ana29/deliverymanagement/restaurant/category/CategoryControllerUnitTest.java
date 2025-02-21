package com.ana29.deliverymanagement.restaurant.category;

import com.ana29.deliverymanagement.restaurant.controller.CategoryController;
import com.ana29.deliverymanagement.restaurant.entity.Category;
import com.ana29.deliverymanagement.restaurant.service.CategoryService;
import com.ana29.deliverymanagement.security.config.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoBean(types = JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = CategoryController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        })
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class CategoryControllerUnitTest {
    //단위테스트 (삭제,검색)

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void testSearchCategory() throws Exception {

    }

//    @Test
//    void testCategoryDelete() throws Exception {
//        UUID uuid = UUID.randomUUID(); //랜덤uuid 생성
//        String userId = "admin";
//
//        mockMvc.perform(delete("/api/categories/"+uuid)
//                .content(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.isDeleted").value(true));
//
//    }
}

package com.ana29.deliverymanagement.restaurant.category;

import com.ana29.deliverymanagement.restaurant.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerFullTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    //public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private final String CATEGORY_ID = "660e8400-e29b-41d4-a716-446655440003";

    //관리자 로그인하여 jwt 토큰생성
    private String getJwtToken() throws Exception {
        String jwtToken = mockMvc.perform(post("/api/users/sign-in")  // URL 앞에 / 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"user5\", \"password\":\"Password5@!\"}")
                )
                .andExpect(status().isOk())  // 로그인 성공
                .andReturn().getResponse().getHeader("Authorization");  // Authorization 헤더에서 JWT 토큰 추출
        System.out.println("JWT Token: " + jwtToken);
        return jwtToken;
    }

    //통합테스트
    @Test
    void testCreateCategory() throws Exception{
        String jwtToken = getJwtToken();

        if (jwtToken != null && !jwtToken.trim().isEmpty()) {
            //로그인 jwt 토큰을 가지고 카테고리 생성(관리자)
            mockMvc.perform(post("/api/categories") //url타입,매핑
                            .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                            .content("{\"foodType\":\"간식\"}") //전달할 json내용
                            .header("Authorization", jwtToken)// 발급받은 JWT 토큰 추가

                    )
                    .andExpect(status().isOk())// 공통 response여서 200확인
                    .andExpect(jsonPath("$.data.foodType").value("간식")); //생성값과 비교
        }else{
            // 토큰이 null이나 빈 값이면, 예외 처리 혹은 디버깅
            System.out.println("JWT Token is missing or empty.");
        }
    }

    @Test
    void testUpdateCategory() throws Exception{
        String jwtToken = getJwtToken();
        mockMvc.perform(put("/api/categories/"+CATEGORY_ID) //url타입,매핑
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .content("{\"foodType\":\"후식\"}") //전달할 json내용
                        .header("Authorization", jwtToken)// 발급받은 JWT 토큰 추가

                )
                .andExpect(status().isOk())// 공통 response여서 200확인
                .andExpect(jsonPath("$.data.foodType").value("후식")); //생성값과 비교
    }

}
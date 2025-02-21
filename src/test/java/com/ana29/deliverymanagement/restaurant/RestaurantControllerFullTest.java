package com.ana29.deliverymanagement.restaurant;

import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerFullTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantService restaurantService;

    private static final String RESTAURANT_ID = "b6a724f8-7eb8-49ef-aa79-59c4b17444ca";

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

    @Test
    void createRestaurant() throws Exception{
        String jwtToken = getJwtToken();
        mockMvc.perform(post("/api/restaurants") //url타입,매핑
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .content("{" +
                                "\"name\":\"돈가스 와와\"" +
                                ",\"ownerId\":\"user2\"" +
                                ",\"category\":\"880e8400-e29b-41d4-a716-446655440005\""+
                                ",\"legalCode\":\"11110\""+
                                ",\"content\":\"맛있는 돈가스집!\""+
                                ",\"operatingHours\":\"10:00~17:00\""+
                                "}") //전달할 json내용
                        .header("Authorization", jwtToken)// 발급받은 JWT 토큰 추가

                )
                .andExpect(status().isOk())// 공통 response여서 200확인
                .andExpect(jsonPath("$.data.name").value("돈가스 와와")); //생성값과 비교

    }

    @Test
    void updateRestaurant() throws Exception{
        String jwtToken = getJwtToken();
        mockMvc.perform(put("/api/restaurants/"+RESTAURANT_ID)
                .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                .content("{\"name\": \"햄햄버거\"" +
                        ",\"content\": \"햄버거집!\"}")
                .header("Authorization", jwtToken)
        )
        .andExpect(status().isOk())// 공통 response여서 200확인
        .andExpect(jsonPath("$.data.name").value("햄햄버거"))
        .andExpect(jsonPath("$.data.content").value("햄버거집!")); //생성값과 비교


    }
}
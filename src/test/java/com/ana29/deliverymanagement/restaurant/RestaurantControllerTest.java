package com.ana29.deliverymanagement.restaurant;

import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantService restaurantService;

    @Test
    @Description("가게 생성테스트")
    void testCreateRestaurant() throws Exception{
        //given
        String jwtToken = getJwtToken();

        //when-then
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
    @Description("가게 수정테스트")
    void testUpdateRestaurant() throws Exception{
        //given
        String jwtToken = getJwtToken();
        String restaurantId = dummyRestaurant();

        //when-then
        mockMvc.perform(put("/api/restaurants/"+restaurantId)
                .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                .content("{\"name\": \"햄햄버거\"" +
                        ",\"content\": \"햄버거집!\"}")
                .header("Authorization", jwtToken)
        )
        .andExpect(status().isOk())// 공통 response여서 200확인
        .andExpect(jsonPath("$.data.name").value("햄햄버거"))
        .andExpect(jsonPath("$.data.content").value("햄버거집!")); //생성값과 비교


    }

    @Test
    @Description("가게 삭제테스트")
    void testDeleteRestaurant() throws Exception{
        //given
        String jwtToken = getJwtToken();
        String restaurantId = dummyRestaurant();

        //when-then
        mockMvc.perform(delete("/api/restaurants/"+restaurantId)
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .header("Authorization", jwtToken)
                )
                .andExpect(status().isOk())// 공통 response여서 200확인
                .andExpect(jsonPath("$.data.deleted").value(true));


    }

    @Test
    @Description("가게 검색필터링 테스트")
    void testSearchRestaurant() throws Exception{
        String jwtToken = getJwtToken();

        mockMvc.perform(get("/api/restaurants/search?name=레스토랑 14")
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .header("Authorization", jwtToken)
                )
                .andExpect(status().isOk())// 공통 response여서 200확인
                .andExpect(jsonPath("$.data[0].name").value("레스토랑 14"));

    }

    //고정된 가게id 생성
    private String dummyRestaurant() throws Exception {
        String jwtToken = getJwtToken();

        MvcResult result = mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"id\":\"b859a60a-0ed7-4129-abf2-49052d42af38\"" +
                                ",\"name\":\"가게1\"" +
                                ",\"ownerId\":\"user2\"" +
                                ",\"category\":\"880e8400-e29b-41d4-a716-446655440005\"" +
                                ",\"legalCode\":\"11110\"" +
                                ",\"content\":\"맛있는집!\"" +
                                ",\"operatingHours\":\"10:00~17:00\"" +
                                "}")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andReturn();  // MvcResult로 반환

        // 생성된 가게 ID 추출
        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(response);
        return jsonNode.get("data").get("id").asText();  // 가게의 ID를 반환
    }

    //관리자 로그인하여 jwt 토큰생성
    private String getJwtToken() throws Exception {
        mockMvc.perform(post("/api/users/sign-up")  // URL 앞에 / 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"adad123\"" +
                                ",\"nickname\":\"add12\"" +
                                ",\"password\":\"adD021234!\"" +
                                ",\"phone\":\"010-1241-1414\""+
                                ",\"email\":\"add@naver.com\"" +
                                ",\"tokenValue\":\"SGhJeWZ3ZFFLOWMwNnZhVGc0NDZaZWx0bXcxdkVKVURPWGc2YkhFSHFTM2RtbXh3RGs3RlhsWmhXYVMFF3MVpuaHE2MDA0amw9PQ==\"}")
                )
                .andExpect(status().is3xxRedirection()); //리다이렉트 확인

        String jwtToken = mockMvc.perform(post("/api/users/sign-in")  // URL 앞에 / 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"adad123\", \"password\":\"adD021234!\"}")
                )
                .andExpect(status().isOk())  // 로그인 성공
                .andReturn().getResponse().getHeader("Authorization");  // Authorization 헤더에서 JWT 토큰 추출
        System.out.println("JWT Token: " + jwtToken);
        return jwtToken;
    }

}
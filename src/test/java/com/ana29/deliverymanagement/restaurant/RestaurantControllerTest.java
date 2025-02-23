package com.ana29.deliverymanagement.restaurant;

import com.ana29.deliverymanagement.restaurant.service.RestaurantService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class RestaurantControllerTest {

    //관리자 로그인하여 jwt 토큰생성
    private static String cachedJwtToken;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RestaurantService restaurantService;

    @Test
    @Description("가게 생성테스트")
    void testCreateRestaurant() throws Exception {
        //given
        String jwtToken = getJwtToken();

        //when-then
        mockMvc.perform(post("/api/restaurants") //url타입,매핑
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .content("{" +
                                "\"name\":\"돈가스 와와\"" +
                                ",\"ownerId\":\"user2\"" +
                                ",\"category\":\"880e8400-e29b-41d4-a716-446655440005\"" +
                                ",\"legalCode\":\"11110\"" +
                                ",\"content\":\"맛있는 돈가스집!\"" +
                                ",\"operatingHours\":\"10:00~17:00\"" +
                                "}") //전달할 json내용
                        .header("Authorization", jwtToken)// 발급받은 JWT 토큰 추가
                )
                .andExpect(status().isOk())// 공통 response여서 200확인
                .andExpect(jsonPath("$.data.name").value("돈가스 와와")) //생성값과 비교
                .andDo(document("restaurant-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        getRequestFieldsSnippet(),
                        getResponseFieldsSnippet()));

    }

    @Test
    @Description("가게 수정테스트")
    void testUpdateRestaurant() throws Exception {
        //given
        String jwtToken = getJwtToken();
        String restaurantId = dummyRestaurant();

        //when-then
        mockMvc.perform(put("/api/restaurants/{id}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"햄햄버거\"," +
                                "\"content\": \"햄버거집!\"," +
                                "\"category\": \"880e8400-e29b-41d4-a716-446655440005\"," +
                                "\"legalCode\": \"11110\"}")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("햄햄버거"))
                .andExpect(jsonPath("$.data.content").value("햄버거집!"))//생성값과 비교
                // 나머지 문서화 스니펫...
                .andDo(document("restaurant-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("음식 카테고리 ID")
                        ),
                        getRequestUpdateFieldSnippet(),
                        getResponseFieldsSnippet()));

    }

    private RequestFieldsSnippet getRequestUpdateFieldSnippet() {
        return requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("음식점 이름"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("음식점 소개내용"),
                fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리 ID"),
                fieldWithPath("legalCode").type(JsonFieldType.STRING).description("음식점 지역코드")
        );
    }


    @Test
    @Description("가게 삭제테스트")
    void testDeleteRestaurant() throws Exception {
        //given
        String jwtToken = getJwtToken();
        String restaurantId = dummyRestaurant();

        //when-then
        mockMvc.perform(delete("/api/restaurants/{id}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .header("Authorization", jwtToken)
                )
                .andExpect(status().isOk())// 공통 response여서 200확인
                .andExpect(jsonPath("$.data.deleted").value(true))
                .andDo(document("restaurant-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("음식점 ID")
                        ),
                        getResponseFieldsSnippet()));

    }

    @Test
    @Description("가게 검색필터링 테스트")
    void testSearchRestaurant() throws Exception {
        String jwtToken = getJwtToken();
        dummyRestaurant();
        mockMvc.perform(get("/api/restaurants/search?name=가게1")
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .header("Authorization", jwtToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("가게1"));

    }

    //고정된 가게id 생성
    private String dummyRestaurant() throws Exception {

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
                        .header("Authorization", cachedJwtToken))
                .andExpect(status().isOk())
                .andReturn();  // MvcResult로 반환

        // 생성된 가게 ID 추출
        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(response);
        return jsonNode.get("data").get("id").asText();  // 가게의 ID를 반환
    }

    private String getJwtToken() throws Exception {
        if (cachedJwtToken != null && !cachedJwtToken.trim().isEmpty()) {
            return cachedJwtToken;
        }

        try {
            mockMvc.perform(post("/api/users/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":\"adad123\"" +
                                    ",\"nickname\":\"add12\"" +
                                    ",\"password\":\"adD021234!\"" +
                                    ",\"phone\":\"010-1241-1414\"" +
                                    ",\"email\":\"add@naver.com\"" +
                                    ",\"tokenValue\":\"SGhJeWZ3ZFFLOWMwNnZhVGc0NDZaZWx0bXcxdkVKVURPWGc2YkhFSHFTM2RtbXh3RGs3RlhsWmhXYVMFF3MVpuaHE2MDA0amw9PQ==\"}")
                    )
                    .andExpect(status().is3xxRedirection());
        } catch (Exception e) {
            System.out.println("회원가입 중 중복 오류 발생 (무시): " + e.getMessage());
        }

        cachedJwtToken = mockMvc.perform(post("/api/users/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"adad123\", \"password\":\"adD021234!\"}")
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader("Authorization");
        System.out.println("JWT Token: " + cachedJwtToken);
        return cachedJwtToken;
    }


    private ResponseFieldsSnippet getResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("code").description("응답 코드"),
                fieldWithPath("status").description("응답 상태"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data.id").type(JsonFieldType.STRING).description("음식점 ID"),
                fieldWithPath("data.name").type(JsonFieldType.STRING).description("음식점 이름"),
                fieldWithPath("data.ownerId").type(JsonFieldType.STRING).description("음식점 사장 ID"),
                fieldWithPath("data.category").type(JsonFieldType.OBJECT).description("음식점 카테고리"),
                fieldWithPath("data.category.id").type(JsonFieldType.STRING).description("카테고리 ID"),
                fieldWithPath("data.category.foodType").type(JsonFieldType.STRING).description("음식 카테고리 이름"),
                fieldWithPath("data.legalCode").type(JsonFieldType.STRING).description("음식점 지역코드"),
                fieldWithPath("data.content").type(JsonFieldType.STRING).description("음식점 소개내용"),
                fieldWithPath("data.operatingHours").type(JsonFieldType.STRING).description("음식점 영업시간"),
                fieldWithPath("data.deleted").type(JsonFieldType.BOOLEAN).description("음식점 삭제 여부"),
                fieldWithPath("data.createdAt").description("음식점 생성시간"),
                fieldWithPath("data.createdBy").description("음식점 생성자"),
                fieldWithPath("data.updatedAt").description("음식점 수정시간"),
                fieldWithPath("data.updatedBy").description("음식점 수정자"),
                fieldWithPath("data.deletedAt").description("음식점 삭제시간"),
                fieldWithPath("data.deletedBy").description("음식점 삭제자"),
                fieldWithPath("data.deleted").type(JsonFieldType.BOOLEAN).description("음식점 삭제 여부"),
                fieldWithPath("data.category.createdAt").description("음식점 생성시간"),
                fieldWithPath("data.category.createdBy").description("음식점 생성자"),
                fieldWithPath("data.category.updatedAt").description("음식점 수정시간"),
                fieldWithPath("data.category.updatedBy").description("음식점 수정자"),
                fieldWithPath("data.category.deletedAt").description("음식점 삭제시간"),
                fieldWithPath("data.category.deletedBy").description("음식점 삭제자"),
                fieldWithPath("data.category.deleted").type(JsonFieldType.BOOLEAN).description("음식점 삭제 여부")

        );
    }

    private RequestFieldsSnippet getRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("음식점 이름"),
                fieldWithPath("ownerId").type(JsonFieldType.STRING).description("음식점 사장 ID"),
                fieldWithPath("category").type(JsonFieldType.STRING).description("음식점 음식유형"),
                fieldWithPath("legalCode").type(JsonFieldType.STRING).description("음식점 지역코드"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("음식점 소개내용"),
                fieldWithPath("operatingHours").type(JsonFieldType.STRING).description("음식점 영업시간")
        );
    }

}
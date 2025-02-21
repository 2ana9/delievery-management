package com.ana29.deliverymanagement.restaurant;

import com.ana29.deliverymanagement.restaurant.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .defaultRequest(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))
                .apply(springSecurity())
                .build();
    }

    private static final String CATEGORY_ID = "660e8400-e29b-41d4-a716-446655440003";

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

    //통합테스트
    @Test
    @Description("음식 카테고리 생성테스트")
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
                    .andExpect(jsonPath("$.data.foodType").value("간식"))
                    .andDo(document("category-create",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName("Authorization").description("JWT 토큰")),
                            getResponseFieldsSnippet()));
        }else{
            System.out.println("JWT Token is missing or empty.");
        }
    }

    @Test
    @Description("음식 카테고리 수정테스트")
    void testUpdateCategory() throws Exception{
        String jwtToken = getJwtToken();
        if (jwtToken != null && !jwtToken.trim().isEmpty()) {
            mockMvc.perform(put("/api/categories/"+CATEGORY_ID) //url타입,매핑
                        .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                        .content("{\"foodType\":\"후식\"}") //전달할 json내용
                        .header("Authorization", jwtToken)// 발급받은 JWT 토큰 추가

                )
                .andExpect(status().isOk())// 공통 response여서 200확인
                .andExpect(jsonPath("$.data.foodType").value("후식")); //생성값과 비교

        }else{
            System.out.println("JWT Token is missing or empty.");
        }
    }

    @Test
    @Description("음식 카테고리 삭제테스트")
    void testDeleteCategory() throws Exception{
        String jwtToken = getJwtToken();
        if (jwtToken != null && !jwtToken.trim().isEmpty()) {
            mockMvc.perform(delete("/api/categories/"+CATEGORY_ID) //url타입,매핑
                            .contentType(MediaType.APPLICATION_JSON) //받는 요청의 타입
                            .header("Authorization", jwtToken)// 발급받은 JWT 토큰 추가
                    )
                    .andExpect(status().isOk())// 공통 response여서 200확인
                    .andExpect(jsonPath("$.data.deleted").value(true)); //생성값과 비교
        }else{

            System.out.println("JWT Token is missing or empty.");
        }


    }

    @Test
    @Description("음식 카테고리 검색테스트")
    void testSearchCategory() throws Exception{
        String jwtToken = getJwtToken();
        if (jwtToken != null && !jwtToken.trim().isEmpty()) {
            mockMvc.perform(get("/api/categories/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwtToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].foodType").value("한식"));
        }else {
            System.out.println("JWT Token is missing or empty.");
        }
    }

    private ResponseFieldsSnippet getResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("code").description("The response code"),
                fieldWithPath("status").description("The response status"),
                fieldWithPath("message").description("A message describing the response"),
                fieldWithPath("data").description("The data object containing category information"),
                fieldWithPath("data.id").type(JsonFieldType.STRING).description("음식 카테고리 ID"),
                fieldWithPath("data.foodType").type(JsonFieldType.STRING).description("카테고리 이름"),
                fieldWithPath("data.deleted").type(JsonFieldType.BOOLEAN).description("삭제 여부")
                .optional(),
                fieldWithPath("data.createdAt").description("The creation date and time of the category"),
                fieldWithPath("data.createdBy").description("The creator of the category"),
                fieldWithPath("data.updatedAt").description("The last update date and time"),
                fieldWithPath("data.updatedBy").description("The last person who updated the category"),
                fieldWithPath("data.deletedAt").description("The deletion date and time (if any)"),
                fieldWithPath("data.deletedBy").description("The person who deleted the category (if any)")
        );
    }



}
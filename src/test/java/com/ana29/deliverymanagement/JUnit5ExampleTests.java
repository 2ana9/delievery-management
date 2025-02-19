package com.ana29.deliverymanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest <- @contoller 어노테이션만 load 되므로 서비스나 다른 컴포넌트, 즉 빈들과의 의존성 연결 실패
@SpringBootTest // ✅ 통합 테스트: 전체 컨텍스트 로드
@AutoConfigureMockMvc // ✅ MockMvc 자동 설정
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class JUnit5ExampleTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void testGetExample() throws Exception {
        // ✅ MockMvc를 사용하여 GET 요청을 실행하고 문서화
        ResultActions result = mockMvc.perform(get("/api/users/sign-in"))
                .andExpect(status().isOk()); // HTTP 200 확인

        // ✅ RestDocs 문서 생성
        result.andDo(document("example-get"));
    }

    @Test
    void testPostExample() throws Exception {
        String requestBody = "{ \"name\": \"John\", \"age\": 30 }";

        ResultActions result = mockMvc.perform(post("/api/example")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated()); // HTTP 201 확인

        result.andDo(document("example-post"));
    }
}

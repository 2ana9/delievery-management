package com.ana29.deliverymanagement.review.controller;


import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.review.dto.CreateReviewRequestDto;
import com.ana29.deliverymanagement.review.dto.ReviewDetailResponseDto;
import com.ana29.deliverymanagement.review.dto.ReviewSearchCondition;
import com.ana29.deliverymanagement.review.dto.UpdateReviewRequestDto;
import com.ana29.deliverymanagement.review.service.ReviewService;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.WebSecurityConfig;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.ana29.deliverymanagement.review.controller.ReviewDtoStub.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoBean(types = JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = ReviewController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        })
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ReviewService reviewService;
    private final String TEST_USERNAME = "testuser";
    private final String MOCK_JWT_TOKEN = "Bearer jwt-token";

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .defaultRequest(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("리뷰 생성 API")
    void createReview() throws Exception {
        // Given
        CreateReviewRequestDto requestDto = getReviewDetailsResponseDtoStub();
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
        ReviewDetailResponseDto responseDto
                = getReviewDetailsResponseDtoStub(TEST_USERNAME);

        when(reviewService.createReview(any(CreateReviewRequestDto.class), anyString()))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andDo(document("review-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        getRequestFieldsSnippet(),
                        getReviewDetailResponseSnippet()));
    }

    @Test
    @DisplayName("리뷰 상세 조회 API")
    void getReviewDetail() throws Exception {
        // Given
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
        ReviewDetailResponseDto responseDto = getReviewDetailsResponseDtoStub(TEST_USERNAME);

        when(reviewService.getReviewDetail(eq(TEST_REVIEW_ID), anyString()))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/reviews/{id}", TEST_REVIEW_ID)
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(document("review-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("리뷰 ID")
                        ),
                        getReviewDetailResponseSnippet()));
    }

    @Test
    @DisplayName("리뷰 전체 조회")
    void getAllReviews() throws Exception {
        // Given
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
        ReviewSearchCondition condition = ReviewDtoStub.createReviewSearchCondition();
        Page<ReviewDetailResponseDto> pageResult = ReviewDtoStub.createReviewsPage();

        when(reviewService.getAllReviews(any(ReviewSearchCondition.class), any(Pageable.class)))
                .thenReturn(pageResult);

        // When & Then
        mockMvc.perform(
                        ReviewDtoStub.applyDefaultSearchParams(
                                        get("/api/reviews"))
                                .header("Authorization", MOCK_JWT_TOKEN)
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(document("get-all-reviews",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        getQueryParametersSnippet(),
                        getAllReviewsResponseSnippet()));
    }

    @Test
    @DisplayName("가게에 달린 리뷰 전체 조회")
    void getReviewsByRestaurantId() throws Exception {
        // Given
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
        ReviewSearchCondition condition = ReviewDtoStub.createReviewSearchCondition();
        Page<ReviewDetailResponseDto> pageResult = ReviewDtoStub.createReviewsPage();

        when(reviewService.getReviewsByRestaurant(eq(TEST_RESTAURANT_ID),any(ReviewSearchCondition.class), any(Pageable.class)))
                .thenReturn(pageResult);

        // When & Then
        mockMvc.perform(
                        ReviewDtoStub.applyDefaultSearchParams(
                                        get("/api/reviews/restaurant"))
                                .param("restaurantId", TEST_RESTAURANT_ID.toString())
                                .header("Authorization", MOCK_JWT_TOKEN)
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(document("get-reviews-by-restaurantId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        getQueryParametersSnippet(
                                parameterWithName("restaurantId").description("레스토랑 ID")),
                        getAllReviewsResponseSnippet()));
    }

    @Test
    @DisplayName("리뷰 수정 API")
    void updateReview() throws Exception {
        // Given
        UpdateReviewRequestDto requestDto = createUpdateReviewRequest();
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
        ReviewDetailResponseDto responseDto = getReviewDetailsResponseDtoStub(TEST_USERNAME);

        when(reviewService.updateReview(eq(TEST_REVIEW_ID), any(UpdateReviewRequestDto.class),any(UserDetailsImpl.class)))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/reviews/{id}", TEST_REVIEW_ID)
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(document("review-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("리뷰 ID")
                        ),
                        getUpdateRequestFieldsSnippet(),
                        getReviewDetailResponseSnippet()));
    }

    @Test
    @DisplayName("리뷰 삭제 API")
    void deleteReview() throws Exception {
        // Given
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
        doNothing().when(reviewService).deleteReview(eq(TEST_REVIEW_ID), anyString());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{id}", TEST_REVIEW_ID)
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isNoContent())
                .andDo(document("review-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("리뷰 ID")
                        ),
                        getNoContentResponseSnippet()));
    }

    private static ResponseFieldsSnippet getNoContentResponseSnippet() {
        return responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                fieldWithPath("data").type(JsonFieldType.NULL).description("데이터 없음")
        );
    }


    private QueryParametersSnippet getQueryParametersSnippet(
            ParameterDescriptor... additionalParameters) {
        List<ParameterDescriptor> commonParameters = Arrays.asList(
                parameterWithName("keyword").description("검색 키워드").optional(),
                parameterWithName("startDate").description("시작 날짜 (yyyy-MM-dd)").optional(),
                parameterWithName("endDate").description("종료 날짜 (yyyy-MM-dd)").optional(),
                parameterWithName("isAsc").description("오름차순 정렬 여부 (기본값: false)").optional(),
                parameterWithName("page").description("페이지 번호").optional(),
                parameterWithName("size").description("페이지 크기").optional()
        );

        List<ParameterDescriptor> allParameters = new ArrayList<>(commonParameters);
        allParameters.addAll(Arrays.asList(additionalParameters));

        return queryParameters(allParameters.toArray(new ParameterDescriptor[0]));
    }

    private static ResponseFieldsSnippet getAllReviewsResponseSnippet() {
        return responseFields(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                fieldWithPath("data.content[].reviewId").type(JsonFieldType.STRING).description("리뷰 ID"),
                fieldWithPath("data.content[].orderId").type(JsonFieldType.STRING).description("주문 ID"),
                fieldWithPath("data.content[].menuId").type(JsonFieldType.STRING).description("메뉴 ID"),
                fieldWithPath("data.content[].restaurantId").type(JsonFieldType.STRING).description("음식점 ID"),
                fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("리뷰 제목"),
                fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                fieldWithPath("data.content[].rating").type(JsonFieldType.NUMBER).description("별점"),
                fieldWithPath("data.content[].menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                fieldWithPath("data.content[].restaurantName").type(JsonFieldType.STRING).description("음식점 이름"),
                fieldWithPath("data.content[].createdBy").type(JsonFieldType.STRING).description("리뷰 작성자"),
                fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("리뷰 작성 시간"),
                fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN)
                        .description("정렬이 비어있는지 여부"),
                fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN)
                        .description("정렬 적용 여부"),
                fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN)
                        .description("정렬 미적용 여부"),
                fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("페이지 "
                        + "오프셋"),
                fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER)
                        .description("페이지 번호"),
                fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER)
                        .description("페이지 크기"),
                fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN)
                        .description("페이징 미적용 여부"),
                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN)
                        .description("정렬이 비어있는지 여부"),
                fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 적용 여부"),
                fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN)
                        .description("정렬 미적용 여부"),
                fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER)
                        .description("현재 페이지 요소 수"),
                fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비어있는지 여부")
        );
    }


    public static UserDetailsImpl createUserDetails(String username, UserRoleEnum role) {
        return new UserDetailsImpl(User.builder()
                .Id(username)
                .password("password")
                .role(role)
                .build());
    }

    private static RequestFieldsSnippet getRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 ID"),
                fieldWithPath("title").type(JsonFieldType.STRING).description("리뷰 제목"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                fieldWithPath("rating").type(JsonFieldType.NUMBER).description("별점")
        );
    }

    private static RequestFieldsSnippet getUpdateRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("title").type(JsonFieldType.STRING).description("리뷰 제목"),
                fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                fieldWithPath("rating").type(JsonFieldType.NUMBER).description("별점")
        );
    }

    private ResponseFieldsSnippet getReviewDetailResponseSnippet() {
        return responseFields(List.of(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                fieldWithPath("data.reviewId").type(JsonFieldType.STRING).description("리뷰 ID"),
                fieldWithPath("data.orderId").type(JsonFieldType.STRING).description("주문 ID"),
                fieldWithPath("data.menuId").type(JsonFieldType.STRING).description("메뉴 ID"),
                fieldWithPath("data.restaurantId").type(JsonFieldType.STRING).description("가게 ID"),
                fieldWithPath("data.title").type(JsonFieldType.STRING).description("리뷰 제목"),
                fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰 내용"),
                fieldWithPath("data.rating").type(JsonFieldType.NUMBER).description("별점"),
                fieldWithPath("data.menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
                fieldWithPath("data.restaurantName").type(JsonFieldType.STRING).description("가게 이름"),
                fieldWithPath("data.createdBy").type(JsonFieldType.STRING).description("리뷰 생성 시간"),
                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("리뷰 생성자")
        ));
    }

}

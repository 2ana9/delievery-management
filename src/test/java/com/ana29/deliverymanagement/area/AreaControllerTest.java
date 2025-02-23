package com.ana29.deliverymanagement.area;

import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.WebSecurityConfig;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.controller.UserAddressController;
import com.ana29.deliverymanagement.user.dto.*;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.user.service.UserAddressService;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Stream;

import static com.ana29.deliverymanagement.area.AreaDtoStub.*;
import static com.ana29.deliverymanagement.order.controller.OrderDtoStub.TEST_ORDER_ID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoBean(types = JpaMetamodelMappingContext.class)
@WebMvcTest(
        controllers = UserAddressController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        })
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class AreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAddressService userAddressService;

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
    @DisplayName("유저 배송지 생성 API")
    void createOrder() throws Exception {
        // Given
        CreateUserAddressRequestDto requestDto = createUserAddressRequestDto();
        CreateUserAddressResponseDto responseDto = createUserAddressResponseDto();
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);

        when(userAddressService.createUserAddress(requestDto, userDetails))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/user/address")
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(document("user-address-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        getCreateRequestFieldsSnippet(),
                        getCreateResponseFieldsSnippet()));
    }

    @Test
    @DisplayName("유저 배송지 조회 API")
    void getOrderDetail() throws Exception {
        // Given
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
        Page<GetUserAddressesResponseDto> responseDto = getUserAddressesResponsePage();

        when(userAddressService.getUserAddresses(any(GetUserAddressesRequestDto.class), any(Pageable.class), any(UserDetailsImpl.class)))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/user/address", TEST_ORDER_ID)
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(document("user-address-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        getUserAddressesResponseSnippet()));
    }

    @Test
    @DisplayName("유저 배송지 수정 API")
    void updateUserAddresses() throws Exception {
        // Given
        UpdateUserAddressRequestDto requestDto = updateUserAddressRequestDto();
        UpdateUserAddressResponseDto responseDto = updateUserAddressResponseDto();
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);

        when(userAddressService.updateUserAddresses(TEST_USER_ADDRESS_ID, requestDto, userDetails))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/user/address/{id}", TEST_USER_ADDRESS_ID)
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(document("user-address-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("배송지 주소 ID")
                        ),
                        updateUserAddressRequestFieldsSnippet(),
                        updateUserAddressResponseFieldsSnippet()));
    }


    @Test
    @DisplayName("유저 대표 배송지 설정 API")
    void setDefaultAddress() throws Exception {
        // Given
        UpdateUserAddressResponseDto responseDto = updateUserAddressResponseDto();
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);

        when(userAddressService.setDefaultAddress(TEST_USER_ADDRESS_ID, userDetails))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/user/address/{id}/default", TEST_USER_ADDRESS_ID)
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(document("user-address-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("배송지 주소 ID")
                        ),
                        updateUserAddressResponseFieldsSnippet()));
    }

    @Test
    @DisplayName("유저 배송지 삭제 API")
    void deleteUserAddresses() throws Exception {
        // Given
        DeleteUserAddressResponseDto responseDto = deleteUserAddressResponseDto();
        UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);

        when(userAddressService.deleteUserAddresses(TEST_USER_ADDRESS_ID, userDetails))
                .thenReturn(responseDto);

        // When & Then
        mockMvc.perform(delete("/api/user/address/{id}", TEST_USER_ADDRESS_ID)
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(document("set-default-address",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 토큰")),
                        pathParameters(
                                parameterWithName("id").description("배송지 주소 ID")
                        ),
                        deleteUserAddressResponseFieldsSnippet()));
    }



    private ResponseFieldsSnippet getCreateResponseFieldsSnippet() {
        List<FieldDescriptor> commonFields = getCommonUserAddressResponseSnippet();

        List<FieldDescriptor> createFields = List.of(
                fieldWithPath("data.userAddressId").type(JsonFieldType.STRING)
                        .description("주소지 ID"),
                fieldWithPath("data.address").type(JsonFieldType.STRING)
                        .description("주소지"),
                fieldWithPath("data.detail").type(JsonFieldType.STRING)
                        .description("상세주소"),
                fieldWithPath("data.defaultAddress").type(JsonFieldType.BOOLEAN)
                        .description("대표주소지설정")
        );

        return responseFields(Stream.concat(commonFields.stream(), createFields.stream()).toList());
    }

    private ResponseFieldsSnippet getUserAddressesResponseSnippet() {
        List<FieldDescriptor> commonFields = getCommonUserAddressResponseSnippet();

        List<FieldDescriptor> createFields = List.of(
                fieldWithPath("data.content[].userAddressId").type(JsonFieldType.STRING)
                        .description("주소지 ID"),
                fieldWithPath("data.content[].address").type(JsonFieldType.STRING)
                        .description("주소지"),
                fieldWithPath("data.content[].detail").type(JsonFieldType.STRING)
                        .description("상세주소"),
                fieldWithPath("data.content[].defaultAddress").type(JsonFieldType.BOOLEAN)
                        .description("대표주소지설정"),
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

        return responseFields(Stream.concat(commonFields.stream(), createFields.stream()).toList());
    }

    private UserDetailsImpl createUserDetails(String username, UserRoleEnum role) {
        return new UserDetailsImpl(User.builder()
                .Id(username)
                .password("password")
                .role(role)
                .build());
    }

    private RequestFieldsSnippet getCreateRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("address").type(JsonFieldType.STRING).description("주소지"),
                fieldWithPath("detail").type(JsonFieldType.STRING).description("상세 주소")
        );
    }

    private List<FieldDescriptor> getCommonUserAddressResponseSnippet() {
        return List.of(
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
        );
    }

    private RequestFieldsSnippet updateUserAddressRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("address").type(JsonFieldType.STRING).description("주소지"),
                fieldWithPath("detail").type(JsonFieldType.STRING).description("상세 주소")
        );
    }

    private ResponseFieldsSnippet updateUserAddressResponseFieldsSnippet() {
        List<FieldDescriptor> commonFields = getCommonUserAddressResponseSnippet();

        List<FieldDescriptor> createFields = List.of(
                fieldWithPath("data.userAddressId").type(JsonFieldType.STRING)
                        .description("주소지 ID"),
                fieldWithPath("data.address").type(JsonFieldType.STRING)
                        .description("주소지"),
                fieldWithPath("data.detail").type(JsonFieldType.STRING)
                        .description("상세주소"),
                fieldWithPath("data.defaultAddress").type(JsonFieldType.BOOLEAN)
                        .description("대표주소지설정")
        );

        return responseFields(Stream.concat(commonFields.stream(), createFields.stream()).toList());
    }

    private ResponseFieldsSnippet deleteUserAddressResponseFieldsSnippet() {
        List<FieldDescriptor> commonFields = getCommonUserAddressResponseSnippet();

        List<FieldDescriptor> createFields = List.of(
                fieldWithPath("data.userAddressId").type(JsonFieldType.STRING)
                        .description("주소지 ID")
        );

        return responseFields(Stream.concat(commonFields.stream(), createFields.stream()).toList());
    }

}
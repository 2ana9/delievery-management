package com.ana29.deliverymanagement.area;

import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.WebSecurityConfig;
import com.ana29.deliverymanagement.user.constant.user.UserRoleEnum;
import com.ana29.deliverymanagement.user.controller.UserAddressController;
import com.ana29.deliverymanagement.user.dto.CreateUserAddressRequestDto;
import com.ana29.deliverymanagement.user.dto.CreateUserAddressResponseDto;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Stream;

import static com.ana29.deliverymanagement.area.AreaDtoStub.createUserAddressRequestDto;
import static com.ana29.deliverymanagement.area.AreaDtoStub.createUserAddressResponseDto;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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

	private ResponseFieldsSnippet getCreateResponseFieldsSnippet() {
		List<FieldDescriptor> commonFields = getCommonOrderResponseSnippet();

		List<FieldDescriptor> createFields = List.of(
				fieldWithPath("data.userAddressId").type(JsonFieldType.STRING)
						.description("주소지 ID"),
				fieldWithPath("data.address").type(JsonFieldType.STRING)
						.description("주소지"),
				fieldWithPath("data.detail").type(JsonFieldType.STRING)
						.description("상세주소")
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

	private List<FieldDescriptor> getCommonOrderResponseSnippet() {
		return List.of(
				fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
				fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
				fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
		);
	}
}
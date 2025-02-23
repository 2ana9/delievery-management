package com.ana29.deliverymanagement.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.WebSecurityConfig;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.controller.UserController;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
import com.ana29.deliverymanagement.user.dto.UserInfoDto;
import com.ana29.deliverymanagement.user.entity.User;
import com.ana29.deliverymanagement.user.service.Userservice;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@MockitoBean(types = JpaMetamodelMappingContext.class)
@WebMvcTest(
	controllers = UserController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	}
)
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private Userservice userService;

	private final String MOCK_JWT_TOKEN = "Bearer jwt-token";
	private final String TEST_USERNAME = "testuser";
	private final UserRoleEnum masterToken = UserRoleEnum.MASTER;

	@BeforeEach
	public void setup(RestDocumentationContextProvider restDocumentation) {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentation))
			.defaultRequest(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))
			.apply(springSecurity())
			.build();
	}


	@Test
	@DisplayName("회원가입 API")
	void signup() throws Exception {
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
		// Given: 회원가입 요청 DTO
		SignupRequestDto signupRequestDto = SignupRequestDto.builder()
			.id(TEST_USERNAME)
			.nickname("testname1")
			.email("test@example.com")
			.password("Password1Q@w")
			.phone("010-1234-5678")
			.tokenValue("RoleTokenValue")
			.build();
		// 회원가입 성공 후 로그인 페이지로 리다이렉트
		String redirectUrl = "/api/users/sign-in";

		when(userService.signup(any(SignupRequestDto.class))).thenReturn(redirectUrl);

		// When & Then
		mockMvc.perform(post("/api/users/sign-up")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequestDto))
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(redirectUrl))
			.andDo(document("user-signup",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("id").description("회원 ID"),
					fieldWithPath("nickname").description("닉네임"),
					fieldWithPath("email").description("이메일"),
					fieldWithPath("password").description("비밀번호"),
					fieldWithPath("phone").description("전화번호"),
					fieldWithPath("tokenValue").description("특정 권한(사장님, 관리자) 회원가입 토큰").optional()
				)
			));
	}

	@Test
	@DisplayName("로그아웃 API")
	void signOut() throws Exception {
		// Given: 테스트용 UserDetails 생성
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
		String redirectUrl = "/api/users/sign-in";

		when(userService.signOut(any(UserDetailsImpl.class), any(HttpServletRequest.class)))
			.thenReturn(redirectUrl);

		// When & Then
		mockMvc.perform(post("/api/users/sign-out")
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(redirectUrl))
			.andDo(document("user-signout",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")
				)
			));
	}

	@Test
	@DisplayName("내 정보 조회 API")
	void getUserInfo() throws Exception {
		// Given: 테스트용 UserDetails와 조회 결과
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.MANAGER);
		List<UserInfoDto> userInfoList = List.of(new UserInfoDto(
			TEST_USERNAME, "testname1", "test@example.com", "010-1234-5678",
			UserRoleEnum.MANAGER));

		when(userService.getUserInfo(any(), anyInt(), anyInt(), anyString(),
			anyBoolean()))
			.thenReturn(userInfoList);

		// When & Then
		mockMvc.perform(get("/api/users/me")
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.param("page", "0")
				.param("size", "10")
				.param("sortBy", "createdAt")
				.param("isAsc", "false"))
			.andExpect(status().isOk())
			.andDo(document("user-get-info",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")
				),
				queryParameters(
					parameterWithName("page").description("페이지 번호").optional(),
					parameterWithName("size").description("페이지 크기").optional(),
					parameterWithName("sortBy").description("정렬 기준").optional(),
					parameterWithName("isAsc").description("오름차순 여부").optional()
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
					fieldWithPath("data.[].id").description("사용자의 고유 ID"),
					fieldWithPath("data.[].nickname").description("사용자의 닉네임"),
					fieldWithPath("data.[].email").description("사용자의 이메일 주소"),
					fieldWithPath("data.[].phone").description("사용자의 전화번호"),
					fieldWithPath("data.[].isAdmin").description("사용자의 권한 (예: CUSTOMER, ADMIN)")
				)
			));
	}

	@Test
	@DisplayName("회원 정보 수정 API")
	void modifyUserInfo() throws Exception {
		// Given: 테스트용 UserDetails와 수정 요청 DTO
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
		UpdateRequestDto updateDto = UpdateRequestDto.builder()
			.nickname("newname12")
			.email("newemail@example.com")
			.phone("010-8765-4321")
			.build();

		UserInfoDto updatedUser = new UserInfoDto(
			TEST_USERNAME, "newname12", "newemail@example.com", "010-8765-4321",
			UserRoleEnum.CUSTOMER);

		when(userService.modifyUserInfo(any(), any(UpdateRequestDto.class)))
			.thenReturn(updatedUser);

		// When & Then
		mockMvc.perform(patch("/api/users/me")
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andDo(document("user-modify-info",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")
				),
				requestFields(
					fieldWithPath("nickname").description("변경할 닉네임"),
					fieldWithPath("email").description("변경할 이메일"),
					fieldWithPath("phone").description("변경할 전화번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
					fieldWithPath("data.id").description("사용자의 고유 ID"),
					fieldWithPath("data.nickname").description("사용자의 닉네임"),
					fieldWithPath("data.email").description("사용자의 이메일 주소"),
					fieldWithPath("data.phone").description("사용자의 전화번호"),
					fieldWithPath("data.isAdmin").description("사용자의 권한 (예: CUSTOMER, ADMIN)")
				)
			));
	}

	@Test
	@DisplayName("회원 탈퇴 API")
	void deleteUser() throws Exception {
		// Given: 테스트용 UserDetails 생성
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
		String redirectUrl = "/api/users/sign-in";

		// 회원 탈퇴의 경우, userService.deleteUser()가 호출되며 리다이렉션 URL을 반환한다고 가정
		doNothing().when(userService).deleteUser(any(UserDetailsImpl.class));

		// When & Then
		mockMvc.perform(delete("/api/users/me")
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(redirectUrl))
			.andDo(document("user-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")
				)
			));
	}

	public UserDetailsImpl createUserDetails(String username, UserRoleEnum role) {
		return new UserDetailsImpl(User.builder()
			.Id(username)
			.password("password")
			.role(role)
			.build());
	}
}


package com.ana29.deliverymanagement.restaurant.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ana29.deliverymanagement.restaurant.dto.MenuDto;
import com.ana29.deliverymanagement.restaurant.dto.MenuRequestDto;
import com.ana29.deliverymanagement.restaurant.dto.MenuUpdateRequestDto;
import com.ana29.deliverymanagement.restaurant.service.MenuService;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.WebSecurityConfig;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
import org.springframework.restdocs.request.QueryParametersSnippet;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@MockitoBean(types = JpaMetamodelMappingContext.class)
@WebMvcTest(
	controllers = MenuController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	})
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class MenuControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MenuService menuService;

	private final String TEST_USERNAME = "testuser";
	private final String MOCK_JWT_TOKEN = "Bearer jwt-token";
	private final UUID TEST_MENU_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
	private final UUID TEST_RESTAURANT_ID = UUID.fromString("550e8400-e29b-41d4-a716"
		+ "-446655440001");

	@BeforeEach
	public void setup(RestDocumentationContextProvider restDocumentation) {
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentation))
			.defaultRequest(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))
			.apply(springSecurity())
			.build();
	}

	@Test
	@DisplayName("메뉴 생성 API")
	void createMenu() throws Exception {
		// Given
		MenuRequestDto requestDto = createMenuRequestDto();
		MenuDto responseDto = createMenuDto();
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.OWNER);

		when(menuService.createMenu(anyString(), any(MenuRequestDto.class)))
			.thenReturn(responseDto);

		// When & Then
		mockMvc.perform(post("/api/menus/add")
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andDo(document("menu-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				getMenuRequestFieldsSnippet(),
				getMenuResponseSnippet()));
	}

	@Test
	@DisplayName("메뉴 단건 조회 API")
	void getMenu() throws Exception {
		// Given
		MenuDto responseDto = createMenuDto();
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.OWNER);

		when(menuService.getMenu(eq(TEST_MENU_ID)))
			.thenReturn(responseDto);

		// When & Then
		mockMvc.perform(get("/api/menus/{menuId}", TEST_MENU_ID)
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isOk())
			.andDo(document("menu-get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				pathParameters(
					parameterWithName("menuId").description("메뉴 ID")
				),
				getMenuResponseSnippet()));
	}

	@Test
	@DisplayName("메뉴 업데이트 API")
	void updateMenu() throws Exception {
		// Given
		MenuUpdateRequestDto requestDto = createMenuUpdateRequestDto();
		MenuDto responseDto = createUpdatedMenuDto();
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.OWNER);

		when(menuService.updateMenu(eq(TEST_MENU_ID), anyString(),
			any(MenuUpdateRequestDto.class)))
			.thenReturn(responseDto);

		// When & Then
		mockMvc.perform(patch("/api/menus/{menuId}", TEST_MENU_ID)
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andDo(document("menu-update",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				pathParameters(
					parameterWithName("menuId").description("메뉴 ID")
				),
				getMenuUpdateRequestFieldsSnippet(),
				getMenuResponseSnippet()));
	}

	@Test
	@DisplayName("메뉴 삭제 API")
	void deleteMenu() throws Exception {
		// Given
		MenuDto responseDto = createDeletedMenuDto();
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.OWNER);

		when(menuService.deleteMenu(eq(TEST_MENU_ID), anyString()))
			.thenReturn(responseDto);

		// When & Then
		mockMvc.perform(delete("/api/menus/{menuId}", TEST_MENU_ID)
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isOk())
			.andDo(document("menu-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				pathParameters(
					parameterWithName("menuId").description("메뉴 ID")
				),
				getMenuResponseSnippet()));
	}

	@Test
	@DisplayName("메뉴 전체 조회 API")
	void getAllMenus() throws Exception {
		// Given
		List<MenuDto> menuList = Arrays.asList(createMenuDto(), createAnotherMenuDto());
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.OWNER);

		when(menuService.getAllMenus(anyString(), anyInt(), anyInt(), anyString(), anyBoolean()))
			.thenReturn(menuList);

		// When & Then
		mockMvc.perform(get("/api/menus")
				.param("page", "0")
				.param("size", "10")
				.param("sortBy", "name")
				.param("isAsc", "true")
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isOk())
			.andDo(document("menu-get-all",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				getQueryParametersSnippet(),
				getAllMenusResponseSnippet()));
	}

	private MenuRequestDto createMenuRequestDto() {
		MenuRequestDto dto = new MenuRequestDto();
		dto.setName("맛있는 메뉴");
		dto.setPrice(15000L);
		dto.setDescription("매콤달콤한 맛이 일품인 메뉴");
		dto.setRestaurantId(TEST_RESTAURANT_ID);
		return dto;
	}

	private MenuUpdateRequestDto createMenuUpdateRequestDto() {
		MenuUpdateRequestDto dto = new MenuUpdateRequestDto();
		dto.setName("새로운 메뉴 이름");
		dto.setPrice(15000L);
		dto.setDescription("맛있는 메뉴 설명 예시");
		return dto;
	}

	private MenuDto createMenuDto() {
		return MenuDto.builder()
			.id(TEST_MENU_ID)
			.name("맛있는 메뉴")
			.price(15000L)
			.description("매콤달콤한 맛이 일품인 메뉴")
			.isDeleted(false)
			.build();
	}

	private MenuDto createUpdatedMenuDto() {
		return MenuDto.builder()
			.id(TEST_MENU_ID)
			.name("새로운 메뉴 이름")
			.price(15000L)
			.description("맛있는 메뉴 설명 예시")
			.isDeleted(false)
			.build();
	}

	private MenuDto createDeletedMenuDto() {
		return MenuDto.builder()
			.id(TEST_MENU_ID)
			.name("맛있는 메뉴")
			.price(15000L)
			.description("매콤달콤한 맛이 일품인 메뉴")
			.isDeleted(true)
			.build();
	}

	private MenuDto createAnotherMenuDto() {
		return MenuDto.builder()
			.id(UUID.randomUUID())
			.name("다른 메뉴")
			.price(12000L)
			.description("달콤한 맛이 일품인 메뉴")
			.isDeleted(false)
			.build();
	}

	public UserDetailsImpl createUserDetails(String username, UserRoleEnum role) {
		return new UserDetailsImpl(User.builder()
			.Id(username)
			.password("password")
			.role(role)
			.build());
	}

	private QueryParametersSnippet getQueryParametersSnippet() {
		return queryParameters(
			parameterWithName("page").description("페이지 번호 (기본값: 0)").optional(),
			parameterWithName("size").description("페이지 크기 (기본값: 10)").optional(),
			parameterWithName("sortBy").description("정렬 기준 (기본값: name)").optional(),
			parameterWithName("isAsc").description("오름차순 정렬 여부 (기본값: true)").optional()
		);
	}

	private org.springframework.restdocs.payload.RequestFieldsSnippet getMenuRequestFieldsSnippet() {
		return requestFields(
			fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 이름"),
			fieldWithPath("price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
			fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 설명"),
			fieldWithPath("restaurantId").type(JsonFieldType.STRING).description("레스토랑 ID")
		);
	}

	private org.springframework.restdocs.payload.RequestFieldsSnippet getMenuUpdateRequestFieldsSnippet() {
		return requestFields(
			fieldWithPath("name").type(JsonFieldType.STRING).description("메뉴 이름"),
			fieldWithPath("price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
			fieldWithPath("description").type(JsonFieldType.STRING).description("메뉴 설명")
		);
	}

	private org.springframework.restdocs.payload.ResponseFieldsSnippet getMenuResponseSnippet() {
		return responseFields(
			fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
			fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
			fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
			fieldWithPath("data.id").type(JsonFieldType.STRING).description("메뉴 ID"),
			fieldWithPath("data.name").type(JsonFieldType.STRING).description("메뉴 이름"),
			fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
			fieldWithPath("data.description").type(JsonFieldType.STRING).description("메뉴 설명"),
			fieldWithPath("data.deleted").type(JsonFieldType.BOOLEAN).description("삭제 여부")
		);
	}

	private org.springframework.restdocs.payload.ResponseFieldsSnippet getAllMenusResponseSnippet() {
		return responseFields(
			fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
			fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
			fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
			fieldWithPath("data").type(JsonFieldType.ARRAY).description("메뉴 목록"),
			fieldWithPath("data[].id").type(JsonFieldType.STRING).description("메뉴 ID"),
			fieldWithPath("data[].name").type(JsonFieldType.STRING).description("메뉴 이름"),
			fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("메뉴 가격"),
			fieldWithPath("data[].description").type(JsonFieldType.STRING).description("메뉴 설명"),
			fieldWithPath("data[].deleted").type(JsonFieldType.BOOLEAN).description("삭제 여부")
		);
	}
}
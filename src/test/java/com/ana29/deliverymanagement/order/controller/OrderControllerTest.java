package com.ana29.deliverymanagement.order.controller;

import com.ana29.deliverymanagement.order.dto.CreateOrderRequestDto;
import com.ana29.deliverymanagement.order.dto.OrderDetailResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderHistoryResponseDto;
import com.ana29.deliverymanagement.order.dto.OrderSearchCondition;
import com.ana29.deliverymanagement.order.service.OrderService;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.ana29.deliverymanagement.order.controller.OrderDtoStub.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoBean(types = JpaMetamodelMappingContext.class)
@WebMvcTest(
	controllers = OrderController.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = WebSecurityConfig.class
		)
	})
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private OrderService orderService;

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
	@DisplayName("주문 생성 API")
	void createOrder() throws Exception {
		// Given
		CreateOrderRequestDto requestDto = getOrderDetailsResponseDtoStub();
		OrderDetailResponseDto responseDto
			= getOrderDetailsResponseDtoStub(TEST_USERNAME);
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);

		when(orderService.createOrder(any(CreateOrderRequestDto.class), anyString()))
			.thenReturn(responseDto);

		// When & Then
		mockMvc.perform(post("/api/orders")
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isCreated())
			.andDo(document("order-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				getRequestFieldsSnippet(),
				getOrderDetailResponseSnippet()));
	}

	@Test
	@DisplayName("주문 상세 조회 API")
	void getOrderDetail() throws Exception {
		// Given
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
		OrderDetailResponseDto responseDto = getOrderDetailsResponseDtoStub(TEST_USERNAME);

		when(orderService.getOrderDetail(eq(TEST_ORDER_ID), anyString()))
			.thenReturn(responseDto);

		// When & Then
		mockMvc.perform(get("/api/orders/{id}", TEST_ORDER_ID)
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isOk())
			.andDo(document("order-detail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				pathParameters(
					parameterWithName("id").description("주문 ID")
				),
				getOrderDetailResponseSnippet()));
	}


	@Test
	@DisplayName("내 주문 내역 조회 API")
	void getOrderHistory() throws Exception {
		// Given
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
		Page<OrderHistoryResponseDto> pageResult = OrderDtoStub.createOrderHistoryPage();

		when(orderService.getOrderHistory(any(OrderSearchCondition.class), any(Pageable.class),
			anyString(), anyList()))
			.thenReturn(pageResult);

		// When & Then
		mockMvc.perform(
				OrderDtoStub.applyDefaultSearchParams(
						get("/api/orders/my"))
					.param("foodTypes", "한식", "중식")
					.header("Authorization", MOCK_JWT_TOKEN)
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isOk())
			.andDo(document("order-history-my",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				getQueryParametersSnippet()
					.and(parameterWithName("foodTypes").description("카테고리 필터 (쉼표로 구분)").optional()),
				getMyOrderResponseSnippet()));
	}


	@Test
	@DisplayName("레스토랑 주문 내역 조회 API")
	void getRestaurantOrderHistory() throws Exception {
		// Given
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.OWNER);
		Page<OrderHistoryResponseDto> pageResult = OrderDtoStub.createOrderHistoryPage();

		when(orderService.getRestaurantOrderHistory(
			any(OrderSearchCondition.class),
			any(Pageable.class),
			any(UserDetailsImpl.class),
			eq(TEST_RESTAURANT_ID)))
			.thenReturn(pageResult);

		// When & Then
		mockMvc.perform(
				OrderDtoStub.applyDefaultSearchParams(
						get("/api/orders/restaurant"))
					.param("restaurantId", TEST_RESTAURANT_ID.toString())
					.header("Authorization", MOCK_JWT_TOKEN)
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isOk())
			.andDo(document("order-history-restaurant",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				getQueryParametersSnippet(
					parameterWithName("restaurantId").description("레스토랑 ID")),
				getMyOrderResponseSnippet()));
	}

	@Test
	@DisplayName("주문 취소 API")
	void cancelOrder() throws Exception {
		// Given
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.CUSTOMER);
		OrderDetailResponseDto responseDto = getCanceledOrderResponseDtoStub(TEST_USERNAME);

		when(orderService.cancelOrder(eq(TEST_ORDER_ID), anyString()))
			.thenReturn(responseDto);

		// When & Then
		mockMvc.perform(patch("/api/orders/{id}/cancel", TEST_ORDER_ID)
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isOk())
			.andDo(document("order-cancel",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				pathParameters(
					parameterWithName("id").description("주문 ID")
				),
				getOrderCancelResponseSnippet()));
	}

	@Test
	@DisplayName("주문 삭제 API")
	void deleteOrder() throws Exception {
		// Given
		UserDetailsImpl userDetails = createUserDetails(TEST_USERNAME, UserRoleEnum.MASTER);
		doNothing().when(orderService).deleteOrder(eq(TEST_ORDER_ID), anyString());

		// When & Then
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/orders/{id}", TEST_ORDER_ID)
				.header("Authorization", MOCK_JWT_TOKEN)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
			.andExpect(status().isNoContent())
			.andDo(document("order-delete",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestHeaders(
					headerWithName("Authorization").description("JWT 토큰")),
				pathParameters(
					parameterWithName("id").description("주문 ID")
				),
				getNoContentResponseSnippet()));
	}

	private ResponseFieldsSnippet getNoContentResponseSnippet() {
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
			parameterWithName("statuses").description("주문 상태 필터 (쉼표로 구분)").optional(),
			parameterWithName("startDate").description("시작 날짜 (yyyy-MM-dd)").optional(),
			parameterWithName("endDate").description("종료 날짜 (yyyy-MM-dd)").optional(),
			parameterWithName("isAsc").description("오름차순 정렬 여부 (기본값: false)").optional(),
			parameterWithName("page").description("페이지 번호").optional(),
			parameterWithName("size").description("페이지 크기").optional(),
			parameterWithName("sortBy").description("정렬기준 (기본값: 생성일자)").optional()
		);

		List<ParameterDescriptor> allParameters = new ArrayList<>(commonParameters);
		allParameters.addAll(Arrays.asList(additionalParameters));

		return queryParameters(allParameters.toArray(new ParameterDescriptor[0]));
	}

	private ResponseFieldsSnippet getMyOrderResponseSnippet() {
		return responseFields(
			fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
			fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
			fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
			fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("주문 목록"),
			fieldWithPath("data.content[].orderId").type(JsonFieldType.STRING).description("주문 ID"),
			fieldWithPath("data.content[].restaurantId").type(JsonFieldType.STRING)
				.description("음식점 ID"),
			fieldWithPath("data.content[].restaurantName").type(JsonFieldType.STRING)
				.description("음식점 이름"),
			fieldWithPath("data.content[].foodType").type(JsonFieldType.STRING)
				.description("식당 카테고리"),
			fieldWithPath("data.content[].menuName").type(JsonFieldType.STRING)
				.description("메뉴 이름"),
			fieldWithPath("data.content[].orderStatus").type(JsonFieldType.STRING)
				.description("주문 상태"),
			fieldWithPath("data.content[].orderType").type(JsonFieldType.STRING)
				.description("주문 타입"),
			fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING)
				.description("주문 생성 시간"),
			fieldWithPath("data.content[].updatedAt").type(JsonFieldType.STRING)
				.description("주문 수정 시간"),
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

	private RequestFieldsSnippet getRequestFieldsSnippet() {
		return requestFields(
			fieldWithPath("menuId").type(JsonFieldType.STRING).description("메뉴 ID"),
			fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("주문 수량"),
			fieldWithPath("orderRequest").type(JsonFieldType.STRING).description("주문 요청사항")
				.optional(),
			fieldWithPath("paymentType").type(JsonFieldType.STRING)
				.description("결제 방식(CREDIT_CARD)"),
			fieldWithPath("orderType").type(JsonFieldType.STRING)
				.description("주문 타입 (ONLINE, OFFLINE)"),
			fieldWithPath("userAddressId").type(JsonFieldType.STRING).description("배송지 주소 ID")
				.optional()
		);
	}

	public UserDetailsImpl createUserDetails(String username, UserRoleEnum role) {
		return new UserDetailsImpl(User.builder()
			.Id(username)
			.password("password")
			.role(role)
			.build());
	}

	private List<FieldDescriptor> getCommonOrderResponseSnippet() {
		return List.of(
			fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
			fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
			fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
			fieldWithPath("data.orderId").type(JsonFieldType.STRING).description("주문 ID"),
			fieldWithPath("data.orderStatus").type(JsonFieldType.STRING).description("주문 상태"),
			fieldWithPath("data.quantity").type(JsonFieldType.NUMBER).description("주문 수량"),
			fieldWithPath("data.orderRequest").type(JsonFieldType.STRING).description("주문 요청사항"),
			fieldWithPath("data.orderType").type(JsonFieldType.STRING).description("주문 타입"),
			fieldWithPath("data.addressInfo.userAddressId").type(JsonFieldType.STRING).description("주소 ID"),
			fieldWithPath("data.addressInfo.address").type(JsonFieldType.STRING).description("주소"),
			fieldWithPath("data.addressInfo.detail").type(JsonFieldType.STRING).description("상세 주소"),
			fieldWithPath("data.addressInfo.defaultAddress").type(JsonFieldType.BOOLEAN).description("기본 배송지 여부"),
			fieldWithPath("data.orderedAt").type(JsonFieldType.STRING).description("주문 시간"),
			fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("주문 업데이트 시간"),
			fieldWithPath("data.restaurantId").type(JsonFieldType.STRING).description("음식점 ID"),
			fieldWithPath("data.restaurantName").type(JsonFieldType.STRING).description("음식점 이름"),
			fieldWithPath("data.menuName").type(JsonFieldType.STRING).description("메뉴 이름"),
			fieldWithPath("data.menuPrice").type(JsonFieldType.NUMBER).description("메뉴 가격"),
			fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("총 주문 금액"),
			fieldWithPath("data.paymentStatus").type(JsonFieldType.STRING).description("결제 상태"),
			fieldWithPath("data.paymentType").type(JsonFieldType.STRING).description("결제 방식"),
			fieldWithPath("data.externalPaymentId").type(JsonFieldType.STRING)
				.description("외부 결제 ID"),
			fieldWithPath("data.paidAt").type(JsonFieldType.STRING).description("결제 시간"),
			fieldWithPath("data.createdBy").type(JsonFieldType.STRING).description("주문 생성자")
		);
	}

	private ResponseFieldsSnippet getOrderCancelResponseSnippet() {
		List<FieldDescriptor> commonFields = getCommonOrderResponseSnippet();

		List<FieldDescriptor> cancelFields = List.of(
			fieldWithPath("data.orderStatus").type(JsonFieldType.STRING)
				.description("주문 상태(CANCELLED)"),
			fieldWithPath("data.paymentStatus").type(JsonFieldType.STRING)
				.description("결제 상태(REFUNDED)"),
			fieldWithPath("data.refundedAt").type(JsonFieldType.STRING).description("환불 시간")
		);

		return responseFields(Stream.concat(commonFields.stream(), cancelFields.stream()).toList());
	}

	private ResponseFieldsSnippet getOrderDetailResponseSnippet() {
		List<FieldDescriptor> commonFields = getCommonOrderResponseSnippet();

		List<FieldDescriptor> detailFields = List.of(
			fieldWithPath("data.refundedAt").type(JsonFieldType.NULL).description("환불 시간")
		);

		return responseFields(Stream.concat(commonFields.stream(), detailFields.stream()).toList());
	}

}
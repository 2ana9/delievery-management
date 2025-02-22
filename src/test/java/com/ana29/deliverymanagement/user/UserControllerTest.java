package com.ana29.deliverymanagement.user;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.AuthorityConfig;
import com.ana29.deliverymanagement.security.config.WebSecurityConfig;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.security.jwt.RedisTokenBlacklist;
import com.ana29.deliverymanagement.security.service.SecurityContextRedisService;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.ana29.deliverymanagement.user.controller.UserController;
import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
import com.ana29.deliverymanagement.user.dto.UserInfoDto;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import com.ana29.deliverymanagement.user.service.Userservice;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    private AuthorityConfig authorityConfig;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private SecurityContextRedisService redisService;
    @MockitoBean
    private RedisTokenBlacklist redisTokenBlacklist;

    @MockitoBean
    private UserController userController;
    @MockitoBean
    private Userservice userService;

    private final String MOCK_JWT_TOKEN = "Bearer jwt-token";
    private final String TEST_USERNAME = "testuser";
    private final UserRoleEnum masterToken = UserRoleEnum.MASTER;

    // masterTokenValue를 non-final 필드로 선언
    private String masterTokenValue = authorityConfig.getMasterSignupKey() ;

    // 테스트용 UserDetailsImpl 생성 메서드 (구현에 맞게 수정)
    private UserDetailsImpl createUserDetails() {
        return UserDetailsImpl.builder()
                .id(TEST_USERNAME)
                .password(passwordEncoder.encode("Password12!@"))
                .nickname("testname1")
                .email("test@example.com")
                .phone("010-1234-5678")
                .role(masterToken)
                .enabled(true)
                .build();
    }

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {
        // 먼저, authorityConfig.getMasterSignupKey()가 "expected-master-signup-key"를 반환하도록 stub 처리합니다.
        when(authorityConfig.getMasterSignupKey()).thenReturn("expected-master-signup-key");
        // 그리고 stub된 값을 할당합니다.
        masterTokenValue = authorityConfig.getMasterSignupKey();

        // 의존성 주입 (ReflectionTestUtils 등을 사용)
        ReflectionTestUtils.setField(userController, "userService", userService);
        ReflectionTestUtils.setField(userService, "authorityConfig", authorityConfig);

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .defaultRequest(post("/").with(SecurityMockMvcRequestPostProcessors.csrf().asHeader()))
                .apply(springSecurity())
                .build();
    }



    @Test
    @DisplayName("회원가입 API")
    void signup() throws Exception {
        // Given: 회원가입 요청 DTO
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .id(TEST_USERNAME)
                .nickname("testname1")
                .email("test@example.com")
                .password("password!Q@w")
                .phone("010-1234-5678")
                .tokenValue(masterTokenValue)
                .build();
        // 회원가입 성공 후 로그인 페이지로 리다이렉트
        String redirectUrl = "/api/users/sign-in";

        when(userService.signup(any(SignupRequestDto.class))).thenReturn(redirectUrl);

        // When & Then
        mockMvc.perform(post("/api/users/sign-up")
                        .with(SecurityMockMvcRequestPostProcessors.anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDto)))
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
                                fieldWithPath("tokenValue").description("회원가입 토큰")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃 API")
    void signOut() throws Exception {
        // Given: 테스트용 UserDetails 생성
        UserDetailsImpl userDetails = createUserDetails();
        String redirectUrl = "/api/users/sign-in";

        when(userService.signOut(any(UserDetailsImpl.class), any(HttpServletRequest.class)))
                .thenReturn(redirectUrl);

        // When & Then
        mockMvc.perform(post("/api/users/sign-out")
                        .header("Authorization", MOCK_JWT_TOKEN)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
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
        UserDetailsImpl userDetails = createUserDetails();
        List<UserInfoDto> userInfoList = List.of(new UserInfoDto(
                TEST_USERNAME, "testname1", "test@example.com", "010-1234-5678", UserRoleEnum.MANAGER));

        when(userService.getUserInfo(any(UserDetailsImpl.class), anyInt(), anyInt(), anyString(), anyBoolean()))
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
                        pathParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sortBy").description("정렬 기준"),
                                parameterWithName("isAsc").description("오름차순 여부")
                        )
                ));
    }

    @Test
    @DisplayName("회원 정보 수정 API")
    void modifyUserInfo() throws Exception {
        // Given: 테스트용 UserDetails와 수정 요청 DTO
        UserDetailsImpl userDetails = createUserDetails();
        UpdateRequestDto updateDto = UpdateRequestDto.builder()
                .nickname("newname12")
                .email("newemail@example.com")
                .phone("010-8765-4321")
                .build();

        when(userService.modifyUserInfo(any(UserDetailsImpl.class), any(UpdateRequestDto.class)))
                .thenReturn(updateDto);

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
                        )
                ));
    }

    @Test
    @DisplayName("회원 탈퇴 API")
    void deleteUser() throws Exception {
        // Given: 테스트용 UserDetails 생성
        UserDetailsImpl userDetails = createUserDetails();
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
}


//package com.ana29.deliverymanagement.mvc;
//
//import com.ana29.deliverymanagement.security.admin.AdminConfig;
//import com.ana29.deliverymanagement.user.controller.UserController;
//import com.ana29.deliverymanagement.user.dto.SignupRequestDto;
//import com.ana29.deliverymanagement.user.dto.UpdateRequestDto;
//import com.ana29.deliverymanagement.user.dto.UserInfoDto;
//import com.ana29.deliverymanagement.global.exception.GlobalExceptionHandler;
//import com.ana29.deliverymanagement.user.repository.UserRepository;
//import com.ana29.deliverymanagement.security.UserDetailsImpl;
//import com.ana29.deliverymanagement.externalApi.kakao.KakaoService;
//import com.ana29.deliverymanagement.user.service.Userservice;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.http.HttpHeaders.LOCATION;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//
//@ExtendWith(MockitoExtension.class)
//public class UserControllerTest {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private MockMvc mockMvc;
//    @InjectMocks
//    private UserController userController;
//
//    @Mock
//    private Userservice userService;
//    @Mock
//    private PasswordEncoder passwordEncoder;
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private KakaoService kakaoService;
//
//    @Mock
//    private AdminConfig adminConfig;
//
//    @BeforeEach
//    public void setup() {
//        // MockitoAnnotations를 사용하여 목 객체 초기화
//        MockitoAnnotations.openMocks(this);
//
//        // 강제로 Userservice의 adminConfig 필드에 adminConfig mock 주입
//        ReflectionTestUtils.setField(userService, "adminConfig", adminConfig);
//
//        // **중요:** userController의 userService 필드에 userService 객체를 주입
//        ReflectionTestUtils.setField(userController, "userService", userService);
//
////
////        // 간단한 ViewResolver를 추가하여 "redirect:" 접두사가 붙은 뷰를 처리
////        ViewResolver viewResolver = new ViewResolver() {
////            private final Logger logger = LoggerFactory.getLogger("CustomViewResolver");
////
////            @Override
////            public View resolveViewName(String viewName, Locale locale) throws Exception {
////                logger.info("Resolving view name: {}", viewName);
////                if (viewName.startsWith("redirect:")) {
////                    String redirectUrl = viewName.substring("redirect:".length());
////                    logger.info("Creating RedirectView with URL: {}", redirectUrl);
////                    // redirectUrl를 사용해서 RedirectView 생성
////                    RedirectView redirectView = new RedirectView(redirectUrl, false);
////                    redirectView.setExposeModelAttributes(false);
////                    return redirectView;
////                }
////                logger.info("Creating InternalResourceView for view: {}", viewName);
////                return new InternalResourceView("/WEB-INF/views/" + viewName + ".html");
////            }
////        };
//
//
//        // standaloneSetup을 사용하여 컨트롤러만 단위 테스트
//        mockMvc = MockMvcBuilders.standaloneSetup(userController)
//                .setControllerAdvice(new GlobalExceptionHandler()) // 필요 시 추가
//                .setValidator(new LocalValidatorFactoryBean())
////                .setViewResolvers(viewResolver)
//                .build();
//    }
//
//    @Test
//    public void testSignUpPage() throws Exception {
//        // GET /api/users/sign-up 요청 시 "signup" 뷰가 반환되어야 함
//        mockMvc.perform(get("/api/users/sign-up"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("signup"));
//    }
//
//    @Test
//    public void testSignUpPost() throws Exception {
//        // 회원가입 POST 요청 테스트
//        SignupRequestDto signupRequestDto = new SignupRequestDto();
//        signupRequestDto.setId("testId");
//        signupRequestDto.setNickname("testNick");
//        signupRequestDto.setEmail("test@example.com");
//        signupRequestDto.setPassword("testPassword");
//        signupRequestDto.setPhone("010-1111-1111");
//
//
//        String redirectUrl = "/api/users/sign-in";
//        when(userService.signup(signupRequestDto)).thenReturn(redirectUrl);
//
//        mockMvc.perform(post("/api/users/sign-up")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(signupRequestDto)))
//                .andDo(print())
//                .andExpect(header().exists(LOCATION))
//                .andExpect(status().isFound())
//                .andExpect(redirectedUrl(redirectUrl))
//                .andExpect(status().is3xxRedirection());
//
//        verify(userService, times(1)).signup(any(SignupRequestDto.class));
//    }
//
//    @Test
//    public void testSignInPage() throws Exception {
//        // GET /api/users/sign-in 요청 시 "login" 뷰 반환
//        mockMvc.perform(get("/api/users/sign-in"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("login"));
//    }
//
//    @Test
//    public void testSignOut() throws Exception {
//        String redirectUrl = "/api/users/sign-in";
//        when(userService.signOut(any())).thenReturn(redirectUrl);
//
//        mockMvc.perform(post("/api/users/sign-out"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(redirectUrl));
//
//        verify(userService, times(1)).signOut(any());
//    }
//
//    @Test
//    public void testGetUserInfo() throws Exception {
//        UserInfoDto userInfoDto = new UserInfoDto("testId", "testNick", "test@example.com", "010-1111-1111", false);
//        when(userService.getUserInfo(any(UserDetailsImpl.class))).thenReturn(List.of(userInfoDto));
//
//        // POST /api/users/user-info 테스트
//        mockMvc.perform(post("/api/users/user-info")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userInfoDto))));
//
//        verify(userService, times(1)).getUserInfo(any(UserDetailsImpl.class));
//    }
//
//    @Test
//    public void testModifyUserInfo() throws Exception {
//        UpdateRequestDto updateDto = new UpdateRequestDto();
//        updateDto.setNickname("newNick");
//        updateDto.setEmail("new@example.com");
//        updateDto.setPhone("010-2222-2222");
//        updateDto.setCurrentAddress("New Address");
//
//        UserInfoDto updatedInfo = new UserInfoDto("testId", "newNick", "new@example.com", "010-2222-2222", false);
//        when(userService.modifyUserInfo(any(UserDetailsImpl.class), any(UpdateRequestDto.class)))
//                .thenReturn(List.of(updatedInfo));
//
//        mockMvc.perform(patch("/api/users/me")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDto)))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(List.of(updatedInfo))));
//
//        verify(userService, times(1)).modifyUserInfo(any(UserDetailsImpl.class), any(UpdateRequestDto.class));
//    }
//
//    @Test
//    public void testDeleteUser() throws Exception {
//        UpdateRequestDto deleteDto = new UpdateRequestDto(); // 탈퇴 요청 DTO (필요한 필드 설정)
//        doNothing().when(userService).deleteUser(any(UserDetailsImpl.class), any(UpdateRequestDto.class));
//
//        mockMvc.perform(delete("/api/users/me")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(deleteDto)))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/api/users/sign-in"));
//
//        verify(userService, times(1)).deleteUser(any(UserDetailsImpl.class), any(UpdateRequestDto.class));
//    }
//
//    @Test
//    public void testKakaoCallback() throws Exception {
//        String code = "sampleCode";
//        // 예를 들어, kakaoService.kakaoLogin()이 "Bearer sampleJwtToken"을 반환한다고 가정
//        String tokenWithBearer = "Bearer sampleJwtToken";
//        when(kakaoService.kakaoLogin(eq(code))).thenReturn(tokenWithBearer);
//
//        mockMvc.perform(get("/api/users/kakao/callback").param("code", code))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/api/users/sign-in"));
//
//        verify(kakaoService, times(1)).kakaoLogin(eq(code));
//    }
//}

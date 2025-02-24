package com.ana29.deliverymanagement.security.config;


import com.ana29.deliverymanagement.global.exception.CustomAccessDeniedHandler;
import com.ana29.deliverymanagement.security.jwt.JwtAuthenticationFilter;
import com.ana29.deliverymanagement.security.jwt.JwtAuthorizationFilter;
import com.ana29.deliverymanagement.security.jwt.JwtUtil;
import com.ana29.deliverymanagement.security.jwt.RedisTokenBlacklist;
import com.ana29.deliverymanagement.security.service.CachedUserDetailsService;
import com.ana29.deliverymanagement.user.constant.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;

import java.net.http.HttpRequest;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final CachedUserDetailsService userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final RedisTokenBlacklist redisTokenBlacklist;
	private final ObjectMapper objectMapper;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
		throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		return filter;
	}

	@Bean
	public JwtAuthorizationFilter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService, redisTokenBlacklist,
			objectMapper);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF 설정
		http.csrf(AbstractHttpConfigurer::disable);

		// 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
		http.sessionManagement((sessionManagement) ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests((authorizeHttpRequests) ->
			authorizeHttpRequests
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
				.permitAll() // resources 접근 허용 설정
				.requestMatchers("/").permitAll()
				.requestMatchers("/api/users/sign-in", "/api/users/sign-up", "/api/users/kakao/**",
					"/api/v2/**",
					"/api/users/me"
				).permitAll()
				.requestMatchers("/api/gemini/**").hasAuthority(UserRoleEnum.OWNER.getAuthority())
				.requestMatchers("/api/redis/**").hasAuthority(UserRoleEnum.MASTER.getAuthority())
				.anyRequest().authenticated() // 그 외 모든 요청 인증처리
		).exceptionHandling(ex -> ex
			.accessDeniedHandler(new CustomAccessDeniedHandler())
		);

//        http.formLogin((formLogin) ->
//                formLogin
//                        .loginPage("/api/users/sign-in").permitAll()
//        );

		// 필터 관리
		http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter(),
			UsernamePasswordAuthenticationFilter.class);

		// SecurityContext 설정: 세션을 사용하지 않음
		http.securityContext(context ->
			context.securityContextRepository(new NullSecurityContextRepository())
		);
		return http.build();
	}
}
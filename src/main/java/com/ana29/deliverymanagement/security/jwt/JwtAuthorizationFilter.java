package com.ana29.deliverymanagement.security.jwt;

import com.ana29.deliverymanagement.security.service.CachedUserDetailsService;
import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.constant.jwt.JwtErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CachedUserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, CachedUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();
//        /api/users/sign-in 의 Get 접속은 검증하지 않음
        return path.equals("/api/users/sign-in") && "GET".equalsIgnoreCase(request.getMethod());
        // 로그인/로그아웃 엔드포인트는 검증하지 않음
        // sign-in의 POST 방식도 검증해야 하나?
        // shouldNotFilter가 없으면 'sign-out' 메소드 후 리다이렉트 되는 'sing-in' (GET) 에서
        // JWT 검증을 하게 됨. sign-out 메소드는 토큰 블랙리스트를 등록하므로
        // sign-in 페이지에서 블랙리스트에 걸려 401 권한 에러.
//        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getJwtFromHeader(request);
        //토큰 블랙리스트 검증
        if (token != null && !token.isEmpty()) {
            if (TokenBlacklist.isTokenBlacklisted(token)) {
                log.info("BLACKLIST VALID");
                log.info("BLACKLIST INfO : " + TokenBlacklist.getBlacklistedTokens().toString());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        if (StringUtils.hasText(token)) {
            log.info("token : " + token);
            if (!jwtUtil.validateToken(token)) {
                log.error(JwtErrorMessage.Error.getGetJwtErrorMessage());
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(token);
            log.info("Claims : " + info);
            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        Object cachedUserDetails = userDetailsService.loadUserByUsername(username);
        log.info("createAuthentication : " + cachedUserDetails.toString());

        if (cachedUserDetails instanceof LinkedHashMap<?, ?> map) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // ✅ LocalDateTime 지원

            // ✅ Redis에서 역직렬화된 데이터 → `UserDetailsImpl` 변환
            UserDetailsImpl userDetails = objectMapper.convertValue(map, UserDetailsImpl.class);
            // ✅ `String`으로 저장된 권한을 `SimpleGrantedAuthority`로 변환
            List<GrantedAuthority> authorities = userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                    .collect(Collectors.toList());

            userDetails.setAuthorities(authorities.stream().map(GrantedAuthority::getAuthority).toList());
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        return new UsernamePasswordAuthenticationToken(cachedUserDetails, null, ((UserDetails) cachedUserDetails).getAuthorities());
    }


}
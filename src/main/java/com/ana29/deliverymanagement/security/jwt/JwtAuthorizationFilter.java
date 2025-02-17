package com.ana29.deliverymanagement.security.jwt;

import com.ana29.deliverymanagement.security.UserDetailsServiceImpl;
import com.ana29.deliverymanagement.security.constant.jwt.JwtErrorMessage;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
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
            log.info("BLACKLIST TEST : " + token);
            if (TokenBlacklist.isTokenBlacklisted(token)) {
                log.info("BLACKLIST VALID");
                log.info("BLACKLIST INfO : " + TokenBlacklist.getBlacklistedTokens().toString());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        log.info("NOT BLACKLIST VALID");
        if (StringUtils.hasText(token)) {

            if (!jwtUtil.validateToken(token)) {
                log.error(JwtErrorMessage.Error.getGetJwtErrorMessage());
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(token);

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
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
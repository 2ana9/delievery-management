package com.ana29.deliverymanagement.security.jwt;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.security.constant.jwt.JwtErrorMessage;
import com.ana29.deliverymanagement.security.service.CachedUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CachedUserDetailsService userDetailsService;
    private final RedisTokenBlacklist redisTokenBlacklist;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("shouldNotFilter check, path: " + path);
//        /api/users/sign-in 의 Get 접속은 검증하지 않음
        return path.equals("/favicon.ico")
                || path.equals("/api/users/sign-in")
                || path.equals("/api/users/sign-up")
                || path.startsWith("/api/users/kakao")
                || path.startsWith("/api/v2");
        // 로그인/로그아웃 엔드포인트는 검증하지 않음
        // sign-in의 POST 방식도 검증해야 하나? No, post 방식에서도 토큰을 가지고 있지 않아야 하므로.
        // shouldNotFilter가 없으면 'sign-out' 메소드 후 리다이렉트 되는 'sing-in' (GET) 에서
        // JWT 검증을 하게 됨. sign-out 메소드는 토큰 블랙리스트를 등록하므로
        // sign-in 페이지에서 블랙리스트에 걸려 401 권한 에러.
//        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getJwtFromHeader(request);
        log.info("jwtUtil.getJwtFromHeader : " + token);
        if (token != null && !token.isEmpty()) {
            // ✅ Redis 기반 토큰 블랙리스트 검증
            if (redisTokenBlacklist.isTokenBlacklisted(token)) {
                log.info("BLACKLIST VALID");
                log.info("BLACKLIST INFO : " + redisTokenBlacklist.getBlacklistedTokens());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            if (!jwtUtil.validateToken(token)) {
                log.error(JwtErrorMessage.Error.getGetJwtErrorMessage());
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(token);
            log.info("Claims info: " + info);
            setAuthentication(info);
            log.info("인증된 토큰을 가지고 있는 사용자입니다.");
            log.info("SecurityContextHolder content : " + SecurityContextHolder.getContext().toString());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<String> authStrings = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            log.info("현재 사용자 권한: {}", authStrings);



        }else {
            // 토큰이 존재하지 않는 경우
            log.info("인증된 토큰이 없습니다.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            ResponseDto responseDto = ResponseDto.failure(
                    HttpStatus.UNAUTHORIZED,
                    "인증이 필요한 서비스입니다."
            );

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
            return;
        }
        filterChain.doFilter(request, response);
    }

    // ✅ 인증 처리 (Claims 정보 기반)
    public void setAuthentication(Claims claims) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // ✅ Claims에서 사용자 정보 가져오기
        String username = claims.getSubject();

        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }


    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails cachedUserDetails = userDetailsService.loadUserByUsername(username);
        log.info("createAuthentication : " + cachedUserDetails.toString());

        // ✅ `String`으로 저장된 권한을 `SimpleGrantedAuthority`로 변환
        List<GrantedAuthority> authorities = cachedUserDetails.getAuthorities()
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(cachedUserDetails, null, authorities);

    }


}
package com.ana29.deliverymanagement.global.exception;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private HttpHeaders headers;

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<ResponseDto> illegalArgumentExceptionHandler(IllegalArgumentException ex, HttpServletRequest request) {
        return handleRedirectException(ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<ResponseDto> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return handleRedirectException(ex.getMessage(), request.getRequestURI());
    }

    // 공통 로직: 특정 URL (예: 회원가입)인 경우, 에러 메시지를 인코딩하여 리다이렉트 응답 생성
    private ResponseEntity<ResponseDto> handleRedirectException(String errorMessage, String requestURI) {
//      에러 메시지를 URL 인코딩하여 쿼리 파라미터로 전달 (예: ?error=메시지)
//      인코딩 없을 시 url을 해석하지 못하여 403 에러
        String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        // 회원가입 URL로 시작하는 경우로 제한 (예: "/api/users/sign-up")
        log.info("handleRedirectException Error message : " + errorMessage.toString());
        if (requestURI.startsWith("/api/users/sign-up")) {
            headers = new HttpHeaders();
            String redirectUrl = "/api/users/sign-up?error=" + encodedErrorMessage;
            headers.setLocation(URI.create(redirectUrl));
            // 302 FOUND 상태와 함께 리다이렉션 헤더를 반환
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        // 그 외의 경우에는 일반적인 BAD_REQUEST 응답 생성
        ResponseDto responseDto = new ResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage
        );
        return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
    }
}
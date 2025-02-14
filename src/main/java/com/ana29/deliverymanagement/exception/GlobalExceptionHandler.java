package com.ana29.deliverymanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<RestApiException> illegalArgumentExceptionHandler(IllegalArgumentException ex, HttpServletRequest request) {
        // 회원가입 URL에서 발생한 예외인 경우
        if (request.getRequestURI().contains("/api/users/sign-up")) {
            HttpHeaders headers = new HttpHeaders();
            // 에러 메시지를 URL 인코딩하여 쿼리 파라미터로 전달 (예: ?error=메시지)
            String encodedErrorMessage = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            String redirectUrl = "/api/users/sign-up?error=" + encodedErrorMessage;
            headers.setLocation(URI.create(redirectUrl));
            // 302 FOUND 상태와 함께 리다이렉션 헤더를 반환
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ResponseEntity<RestApiException> nullPointerExceptionHandler(NullPointerException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({ProductNotFoundException.class})
    @ResponseBody
    public ResponseEntity<RestApiException> notFoundProductExceptionHandler(ProductNotFoundException ex) {
        RestApiException restApiException = new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCustomNotFoundException(CustomNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AlreadyReviewedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAlreadyReviewedException(AlreadyReviewedException e) {
        return e.getMessage();
    }
}

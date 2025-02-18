package com.ana29.deliverymanagement.global.exception;

import com.ana29.deliverymanagement.order.exception.PaymentFailException;
import com.ana29.deliverymanagement.review.exception.AlreadyReviewedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<ExceptionResponse> illegalArgumentExceptionHandler(IllegalArgumentException ex, HttpServletRequest request) {
        // 회원가입 URL에서 발생한 예외인 경우
        if (request.getRequestURI().contains("/api/users/**")) {
            HttpHeaders headers = new HttpHeaders();
            // 에러 메시지를 URL 인코딩하여 쿼리 파라미터로 전달 (예: ?error=메시지)
//            인코딩 없을 시 url을 해석하지 못하여 403 에러
            String encodedErrorMessage = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            String redirectUrl = "/api/users/sign-up?error=" + encodedErrorMessage;
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

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseEntity<RestApiException> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // MethodArgumentNotValidException 발생한 예외인 경우
        if (request.getRequestURI().contains("/api/users/sign-up")) {
            HttpHeaders headers = new HttpHeaders();
            // 에러 메시지를 URL 인코딩하여 쿼리 파라미터로 전달 (예: ?error=메시지)
//            인코딩 없을 시 url을 해석하지 못하여 403 에러
            String encodedErrorMessage = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            String redirectUrl = "/api/users/sign-up?error=" + encodedErrorMessage;
            // 302 FOUND 상태와 함께 리다이렉션 헤더를 반환
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        ExceptionResponse restApiException = ExceptionResponse.of(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(
                // HTTP body
                restApiException,
                // HTTP status code
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ResponseEntity<ExceptionResponse> nullPointerExceptionHandler(NullPointerException ex) {
        ExceptionResponse response = ExceptionResponse.of(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({ProductNotFoundException.class})
    @ResponseBody
    public ResponseEntity<ExceptionResponse> notFoundProductExceptionHandler(ProductNotFoundException ex) {
        ExceptionResponse response = ExceptionResponse.of(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(
                // HTTP body
                response,
                // HTTP status code
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleCustomNotFoundException(CustomNotFoundException e) {
        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(AlreadyReviewedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleAlreadyReviewedException(AlreadyReviewedException e) {
        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CustomForbiddenException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleCustomForbiddenException(CustomForbiddenException e) {
        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    //@Valid로 검증할 때 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ExceptionResponse response = ExceptionResponse.of(e.getBindingResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //@Validated와 함께 사용되는 메서드 파라미터 검증 실패 시 발생하는 예외
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e) {
        ExceptionResponse response = ExceptionResponse.of(e.getConstraintViolations());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //지원하지 않는 HTTP 메서드로 요청이 왔을 때 발생하는 예외
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED.value());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(PaymentFailException.class)
    @ResponseBody
    public ResponseEntity<ExceptionResponse> handlePaymentFailException(PaymentFailException e) {
        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), HttpStatus.BAD_REQUEST.value(), e.getPaymentResult());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

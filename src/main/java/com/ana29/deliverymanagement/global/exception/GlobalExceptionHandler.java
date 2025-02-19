package com.ana29.deliverymanagement.global.exception;

import com.ana29.deliverymanagement.global.dto.ResponseDto;
import com.ana29.deliverymanagement.order.exception.PaymentFailException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private HttpHeaders headers;
	private ModelAndView modelAndView;

	@ExceptionHandler({IllegalArgumentException.class})
	@ResponseBody
	public ResponseEntity<ResponseDto> illegalArgumentExceptionHandler(IllegalArgumentException ex,
		HttpServletRequest request) {
		return handleRedirectException("Type IllegalArgumentExceptionHandler : " + ex.getMessage(),
			request);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseBody
	public ResponseEntity<ResponseDto> methodArgumentNotValidExceptionHandler(
		MethodArgumentNotValidException ex, HttpServletRequest request) {
		// BindingResult에서 모든 에러를 가져온 후 마지막 에러의 기본 메시지만 추출합니다.
		String errorMessage = ex.getBindingResult().getAllErrors().stream()
			.reduce((first, second) -> second)  // 마지막 에러를 선택
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.orElse("유효성 검사 실패");

		return handleRedirectException(
			"Type MethodArgumentNotValidExceptionHandler : " + errorMessage,
			request);
	}
	@ExceptionHandler({NullPointerException.class})
	public ResponseEntity<ResponseDto> nullPointerExceptionHandler(NullPointerException ex, HttpServletRequest request) {
		return handleRedirectException("Type nullPointerExceptionHandler : " + ex.getMessage(),
				request);
	}

	// 공통 로직: 특정 URL (예: 회원가입)인 경우, 에러 메시지를 인코딩하여 리다이렉트 응답 생성
	private ResponseEntity<ResponseDto> handleRedirectException(String errorMessage,
																HttpServletRequest request) {
//      에러 메시지를 URL 인코딩하여 쿼리 파라미터로 전달 (예: ?error=메시지)
//      인코딩 없을 시 url을 해석하지 못하여 403 에러
		String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
		log.info("handleRedirectException Error message : " + errorMessage);

		ResponseDto responseDto = new ResponseDto(
			HttpStatus.BAD_REQUEST.value(),
			HttpStatus.BAD_REQUEST.getReasonPhrase(),
			errorMessage
		);
		headers = new HttpHeaders();
		// 이전 페이지 저장
		String referer = request.getHeader("Referer");
		// redirecturl 이전 페이지 값
		String redirectUrl = (referer != null) ? referer + "?error=" + encodedErrorMessage : "/?error=" + encodedErrorMessage;

		// 회원가입 URL로 시작하는 경우로 제한 (예: "/api/users/sign-up")
		if (request.getRequestURI().startsWith("/api/users")) {
			redirectUrl = "/api/users/sign-up?error=";
			headers.setLocation(URI.create(redirectUrl));
			// 302 FOUND 상태와 함께 리다이렉션 헤더를 반환
			return new ResponseEntity<>(responseDto, headers, HttpStatus.FOUND);
		}
		// 그 외의 경우에는 일반적인 BAD_REQUEST 응답 생성
		headers.setLocation(URI.create(redirectUrl));

		return new ResponseEntity<>(responseDto, headers, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CustomNotFoundException.class)
	@ResponseBody
	public ResponseEntity<ResponseDto> handleCustomNotFoundException(CustomNotFoundException e) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ResponseDto.failure(HttpStatus.NOT_FOUND, e.getMessage()));
	}

	@ExceptionHandler(CustomForbiddenException.class)
	@ResponseBody
	public ResponseEntity<ResponseDto> handleCustomForbiddenException(CustomNotFoundException e) {

		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(ResponseDto.failure(HttpStatus.FORBIDDEN, e.getMessage()));
	}

	@ExceptionHandler(PaymentFailException.class)
    @ResponseBody
    public ResponseEntity<ResponseDto> handlePaymentFailException(PaymentFailException e) {

		return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
			.body(ResponseDto.failure(HttpStatus.PAYMENT_REQUIRED, e.getMessage()));
    }

	@ExceptionHandler(CustomConflictException.class)
	@ResponseBody
	public ResponseEntity<ResponseDto> handlePaymentFailException(CustomConflictException e) {

		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(ResponseDto.failure(HttpStatus.CONFLICT, e.getMessage()));
	}


}

//
//    @ExceptionHandler({ProductNotFoundException.class})
//    @ResponseBody
//    public ResponseEntity<ExceptionResponse> notFoundProductExceptionHandler
//    (ProductNotFoundException ex) {
//        ExceptionResponse response = ExceptionResponse.of(ex.getMessage(), HttpStatus.NOT_FOUND
//        .value());
//        return new ResponseEntity<>(
//                // HTTP body
//                response,
//                // HTTP status code
//                HttpStatus.NOT_FOUND
//        );
//    }
//
//
//
//    @ExceptionHandler(AlreadyReviewedException.class)
//    @ResponseBody
//    public ResponseEntity<ExceptionResponse> handleAlreadyReviewedException
//    (AlreadyReviewedException e) {
//        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), HttpStatus
//        .BAD_REQUEST.value());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    //@Valid로 검증할 때 발생하는 예외
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseBody
//    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException
//    (MethodArgumentNotValidException e) {
//        ExceptionResponse response = ExceptionResponse.of(e.getBindingResult());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    //@Validated와 함께 사용되는 메서드 파라미터 검증 실패 시 발생하는 예외
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseBody
//    public ResponseEntity<ExceptionResponse> handleConstraintViolationException
//    (ConstraintViolationException e) {
//        ExceptionResponse response = ExceptionResponse.of(e.getConstraintViolations());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }
//
//    //지원하지 않는 HTTP 메서드로 요청이 왔을 때 발생하는 예외
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    @ResponseBody
//    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException
//    (HttpRequestMethodNotSupportedException e) {
//        ExceptionResponse response = ExceptionResponse.of(e.getMessage(), HttpStatus
//        .METHOD_NOT_ALLOWED.value());
//        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
//    }
//



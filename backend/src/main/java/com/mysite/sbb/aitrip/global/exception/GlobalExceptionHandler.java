package com.mysite.sbb.aitrip.global.exception;

import com.mysite.sbb.aitrip.global.response.ApiResponse;
import com.mysite.sbb.aitrip.global.response.ErrorCode;
import com.mysite.sbb.aitrip.global.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // BusinessException 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("BusinessException: {} - {}", errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }

    // Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");
        log.warn("Validation failed: {}", message);
        return ResponseEntity
                .status(ResponseCode.BAD_REQUEST.getStatus())
                .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.getCode(), message, null));
    }

    // 예상치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unexpected exception: ", e);
        return ResponseEntity
                .status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
    }
}

package com.example.backend.global.error;

import com.example.backend.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // 1. 일반적인 예외 처리 (Runtime 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiResponse.ErrorResponse error = ApiResponse.ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(e.getMessage())
                .build();

        return ResponseEntity.badRequest().body(
                ApiResponse.<Object>builder().statusCode(400).data(null).error(error).build()
        );
    }

    // 2. @Valid 검증 실패 시 상세 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        List<ApiResponse.FieldError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(err -> new ApiResponse.FieldError(err.getField(), String.valueOf(err.getRejectedValue()), err.getDefaultMessage()))
                .collect(Collectors.toList());

        ApiResponse.ErrorResponse error = ApiResponse.ErrorResponse.builder()
                .code("COMMON_INVALID_PARAMETER")
                .message("요청 파라미터가 잘못되었습니다.")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(
                ApiResponse.<Object>builder().statusCode(400).data(null).error(error).build()
        );
    }
}
package com.sashimi.global.exception;

import com.sashimi.global.trace.TraceIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e,
            HttpServletRequest request
    ) {
        return createErrorResponse(e.getErrorCode(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());

        return createErrorResponse(ErrorCode.INVALID_INPUT_VALUE, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception occurred. path={}", request.getRequestURI(), e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            ErrorCode errorCode,
            HttpServletRequest request
    ) {
        return createErrorResponse(errorCode, errorCode.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            ErrorCode errorCode,
            String message,
            HttpServletRequest request
    ) {
        String traceId = (String) request.getAttribute(TraceIdFilter.TRACE_ID);

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                errorCode.getStatus(),
                errorCode.getCode(),
                message,
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException e,
            HttpServletRequest request
    ) {
        return createErrorResponse(ErrorCode.LOGIN_FAILED, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException e,
            HttpServletRequest request
    ) {
        String message = e.getParameterName() + " 파라미터는 필수입니다.";
        return createErrorResponse(ErrorCode.MISSING_REQUEST_PARAMETER, message, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        String message = e.getName() + " 파라미터 타입이 올바르지 않습니다.";
        return createErrorResponse(ErrorCode.TYPE_MISMATCH, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        return createErrorResponse(ErrorCode.INVALID_REQUEST_BODY, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        return createErrorResponse(ErrorCode.METHOD_NOT_ALLOWED, request);
    }
}
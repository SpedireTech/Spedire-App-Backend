package com.spedire.Spedire.exceptions;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception ex, WebRequest request) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SpedireException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(SpedireException ex, WebRequest request) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidDateException(InvalidDateException ex, WebRequest request) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullValueExpection.class)
    public ResponseEntity<ApiResponse<?>> handleNullValueException(NullValueExpection ex, WebRequest request) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}

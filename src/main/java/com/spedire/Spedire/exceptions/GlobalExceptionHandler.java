package com.spedire.Spedire.exceptions;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ApiResponse<?>> handleNullValueException(NullValueException ex, WebRequest request) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> errorResponse = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}

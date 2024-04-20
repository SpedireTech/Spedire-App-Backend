package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.requests.ForgotPasswordRequest;
import com.spedire.Spedire.dtos.requests.SavePhoneNumberRequest;
import com.spedire.Spedire.dtos.responses.*;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RequestMapping("/api/v1/user/")
@RestController
public class UserController {

    private final UserService userService;


    @GetMapping("test")
    public String test()  {
        return "This is a simple test";
    }

    @PostMapping("verifyPhoneNumber")
    public ResponseEntity<ApiResponse<?>> verifyPhoneNumber(@RequestBody SavePhoneNumberRequest request)  {
        VerifyPhoneNumberResponse response;
        try {
            response = userService.verifyPhoneNumber(request.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).data(response.getToken()).success(true).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @PostMapping("completeRegistration")
    public ResponseEntity<ApiResponse<?>> completeRegistration(@RequestBody CompleteRegistrationRequest request, HttpServletRequest httpServletRequest) {
        try {
            CompleteRegistrationResponse response = userService.completeRegistration(request, httpServletRequest);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(response.isSuccess()).data(response.getData()).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @PostMapping("forgotPassword")
    public ResponseEntity<?> forgotPassword (@RequestBody ForgotPasswordRequest request)  {
        ForgotPasswordResponse response;
        try {
            response = userService.forgotPassword(request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(response.isStatus()).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }

    @PostMapping("resetPassword")
    public ResponseEntity<?> resetPassword (@RequestBody ChangePasswordRequest request) {
        ChangePasswordResponse response;
        try {
            response = userService.resetPassword(request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(response.isStatus()).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }

    }






}

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

    @PostMapping("verifyPhoneNumber")
    public ResponseEntity<ApiResponse<?>> verifyPhoneNumber(@RequestBody SavePhoneNumberRequest request)  {
        System.out.println("Inside Controller");
        VerifyPhoneNumberResponse response = new VerifyPhoneNumberResponse();
        try {
           response = userService.verifyPhoneNumber(request.getPhoneNumber());
           return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).data(response.getToken()).success(true).build());
       } catch (IllegalArgumentException exception) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(response.getMessage()).success(false).build());
       }
    }


//    @PostMapping("complete-registration")
//    public ResponseEntity<ApiResponse<?>> completeRegistration(@RequestBody CompleteRegistrationRequest request, @RequestHeader("Authorization") String token)  {
//        CompleteRegistrationResponse response = new CompleteRegistrationResponse();
//        try {
//            response = userService.completeRegistration(request, token);
//            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(true).build());
//        } catch (IllegalArgumentException exception) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(response.getMessage()).success(false).build());
//        }
//    }


    @PostMapping("complete-registration")
    public ResponseEntity<ApiResponse<?>> completeRegistration(@RequestBody CompleteRegistrationRequest request, HttpServletRequest httpServletRequest) {
        try {
            CompleteRegistrationResponse response = userService.completeRegistration(request, httpServletRequest);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(true).build());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @PostMapping("forgotPassword")
    public ResponseEntity<?> forgotPassword (@RequestBody ForgotPasswordRequest request) throws SpedireException {
        ForgotPasswordResponse response = userService.forgotPassword(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("resetPassword")
    public ResponseEntity<?> resetPassword (@RequestBody ChangePasswordRequest request) throws SpedireException {
        ChangePasswordResponse response = userService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }






}

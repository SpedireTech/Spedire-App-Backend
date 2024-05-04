package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.*;
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


    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegistrationRequest registrationRequest) {
        RegistrationResponse response;
        try {
            response = userService.createUser(registrationRequest);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("Successful").data(response).success(true).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }

    }

    @PostMapping("verifyPhoneNumber")
    public ResponseEntity<ApiResponse<?>> verifyPhoneNumber(HttpServletRequest httpServletRequest, @RequestBody VerifyPhoneNumberRequest request)  {
        VerifyPhoneNumberResponse response;
        try {
            response = userService.verifyPhoneNumber(httpServletRequest, request.isRoute(), request.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("Proceed to enter generated OTP").data(response).success(true).build());
           } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @PostMapping("profile")
    public ResponseEntity<ApiResponse<?>> fetchUserProfile(@RequestHeader("Authorization") String token)  {
        UserProfileResponse response;
        try {
            response = userService.fetchUserProfile(token);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("User Profile").data(response).success(true).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


//    @PostMapping("verifyPhoneNumber")
//    public ResponseEntity<ApiResponse<?>> verifyPhoneNumber(HttpServletRequest httpServletRequest, boolean route, String phoneNumber)  {
//        VerifyPhoneNumberResponse response;
//        try {
//            response = userService.verifyPhoneNumber(httpServletRequest, route, phoneNumber);
//            if (response.getOtp().equals("Kindly verify phone number")) {
//                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiResponse.builder().message(response.getOtp()).data(response.getToken()).success(false).build());
//            } else if (response.getOtp().equals("Error Sending OTP")) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder().message(response.getOtp()).data(response.getToken()).success(false).build());
//            } else {
//                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getOtp()).data(response.getToken()).success(true).build());
//            }
//           } catch (SpedireException exception) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
//        }
//    }



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

    @PostMapping("testing")

    public String testing(){
        return "Spedire server is working";
    }






}

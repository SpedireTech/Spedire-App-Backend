package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.SavePhoneNumberRequest;
import com.spedire.Spedire.dtos.requests.VerifyOtpRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.OtpResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.otp.OtpService;
import com.spedire.Spedire.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/otp")
public class OtpController {

    private final OtpService otpService;
    private final UserService userService;

    @GetMapping("getOtp")
    public ResponseEntity<ApiResponse<?>> getOtp(@RequestBody SavePhoneNumberRequest request)  {
        OtpResponse response;
        try {
            response = otpService.generateOtp(request.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("OTP Available").data(response).success(true).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @PostMapping("verifyOtp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody VerifyOtpRequest request, @RequestHeader("Authorization") String token)  {
        boolean response;
        try {
            response = otpService.verifyOtp(request.getVerificationCode(), token, userService);
            if (response) {
                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("Registration completed").success(response).build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message("Invalid Otp or Phone number").success(response).build());
            }
           } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


}

package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.requests.VerifyOtpRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.CompleteRegistrationResponse;
import com.spedire.Spedire.services.sms.SMSService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/sms/")
public class SMSController {

    private final SMSService smsService;

    @PostMapping("verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody VerifyOtpRequest request, @RequestHeader("Authorization") String token) {
        boolean response = false;
        try {
            response = smsService.checkVerificationCode(request.getVerificationCode(), token);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("Verification Successful").success(response).build());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message("Verification Failed").success(response).build());
        }

    }

}

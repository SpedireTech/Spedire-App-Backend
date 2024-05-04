package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.requests.VerifyOtpRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.CompleteRegistrationResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.sms.SMSService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.spedire.Spedire.controllers.Utils.VERIFICATION_FAILED;
import static com.spedire.Spedire.controllers.Utils.VERIFICATION_SUCCESSFUL;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/sms/")
public class SMSController {

    private final SMSService smsService;

    @PostMapping("verifyOtp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody VerifyOtpRequest request, @RequestHeader(AUTHORIZATION) String token) {
        boolean response = false;
        try {
            response = smsService.checkVerificationCode(request.getVerificationCode(), token);
            if (response) {
                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(VERIFICATION_SUCCESSFUL).success(response).build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(VERIFICATION_FAILED).success(response).build());
            }
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(VERIFICATION_FAILED).success(response).build());
        }
    }

}

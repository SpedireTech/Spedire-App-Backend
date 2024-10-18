package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.SavePhoneNumberRequest;
import com.spedire.Spedire.dtos.requests.VerifyOtpRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.OtpResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.otp.OtpService;
import com.spedire.Spedire.services.user.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.spedire.Spedire.controllers.Utils.*;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

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
            response = otpService.generateOtpWithTermii(request.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(OTP_AVAILABLE).data(response).success(true).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


    @PostMapping("verifyOtp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(@RequestBody VerifyOtpRequest request, @RequestHeader(AUTHORIZATION) String token)  {

        ResponseEntity<ApiResponse<?>> authorizationResponse = validateAuthorization(token);
        if (authorizationResponse != null) {return authorizationResponse;}
        if (request == null || request.getVerificationCode() == null) {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message("Verification code is not passed").success(false).build());}

        boolean response;
        try {
            response = otpService.verifyOtpWithTermii(request.getVerificationCode(), request.getPin_id());
            if (response) {return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(REGISTRATION_COMPLETED).success(response).build());
            } else {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(INVALID_OTP_OR_PHONE_NUMBER).success(response).build());}
           } catch (SpedireException | MessagingException exception) {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


}

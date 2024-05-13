package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utils {

    public static final String CLOSEST_LANDMARK = "Closest Landmarks";
    public static final String NO_LANDMARK = "No Landmarks";
    public static final String OTP_AVAILABLE = "OTP Available";
    public static final String REGISTRATION_COMPLETED = "Registration completed";
    public static final String INVALID_OTP_OR_PHONE_NUMBER = "Invalid Otp or Phone number";
    public static final String VERIFICATION_SUCCESSFUL = "Verification Successful";
    public static final String VERIFICATION_FAILED = "Verification Failed";
    public static final String SUCCESSFUL = "Successful";
    public static final String PROCEED_TO_ENTER_GENERATED_OTP = "Proceed to enter generated OTP";
    public static final String USER_PROFILE = "User Profile";
    public static final String INCOMPLETE_REGISTRATION = "Incomplete registration, Verify your phone number to continue";
    public static final String RESET_INSTRUCTIONS_SENT = "Reset instructions sent to %s";
    public static final String MAIL_DELIVERY_FAILED = "Mail delivery failed";
    public static final String FAIL_TO_SEND_MAIL = "Failed to send mail";
    public static final String AUTHORIZATION_IS_NULL = "Endpoint requires a valid authorization";




    public static ResponseEntity<ApiResponse<?>> validateAuthorization(String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().message(AUTHORIZATION_IS_NULL).success(false).build());
        }
        return null;
    }

}

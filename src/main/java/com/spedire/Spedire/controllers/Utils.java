package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import jakarta.xml.bind.DatatypeConverter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    static final Logger logger = LoggerFactory.getLogger(Utils.class);
    public static final String CLOSEST_LANDMARK = "Closest Landmarks";
    public static final String NO_LANDMARK = "No Landmarks";
    public static final String OTP_AVAILABLE = "OTP Available";
    public static final String REGISTRATION_COMPLETED = "Registration completed";
    public static final String INVALID_OTP_OR_PHONE_NUMBER = "Invalid Otp or Phone number";
    public static final String VERIFICATION_SUCCESSFUL = "Verification Successful";
    public static final String VERIFICATION_FAILED = "Verification Failed";
    public static final String OTP_SENT_SUCCESSFULLY = "OTP sent successfully";
    public static final String PROCEED_TO_ENTER_GENERATED_OTP = "Proceed to enter generated OTP";
    public static final String USER_DASHBOARD_INFO = "Dashboard Information";
    public static final String INCOMPLETE_REGISTRATION = "Incomplete registration, Verify your phone number to continue";
    public static final String RESET_INSTRUCTIONS_SENT = "Reset instructions sent to %s";
    public static final String MAIL_DELIVERY_FAILED = "Mail delivery failed";
    public static final String FAIL_TO_SEND_MAIL = "Failed to send mail";
    public static final String AUTHORIZATION_IS_NULL = "Endpoint requires a valid authorization";
    public static final String  UPGRADE_SUCCESSFUL = "Upgrade Successful";




    public static ResponseEntity<ApiResponse<?>> validateAuthorization(String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder().message(AUTHORIZATION_IS_NULL).success(false).build());
        }
        return null;
    }


    public static String extractMessageFromResponseBody(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getString("message");
        } catch (Exception e) {
            return "Payment verification failed";
        }

    }

    public static boolean verifySignature(String payload, String signature, String secretKey) {
        try {
            String HMAC_SHA512 = "HmacSHA512";
            byte[] byteKey = secretKey.getBytes("UTF-8");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            Mac sha512_HMAC = Mac.getInstance(HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            byte[] macData = sha512_HMAC.doFinal(payload.getBytes("UTF-8"));
            String expectedSignature = DatatypeConverter.printHexBinary(macData).toLowerCase();
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            logger.error("Error verifying signature", e);
            return false;
        }
    }


}

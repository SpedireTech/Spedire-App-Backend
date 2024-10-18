package com.spedire.Spedire.services.otp;

import com.spedire.Spedire.dtos.responses.OtpResponse;
import com.spedire.Spedire.services.user.UserService;
import jakarta.mail.MessagingException;

import java.util.Map;


public interface OtpService {

    OtpResponse generateOtp(String phoneNumber);

    String generateOtpWithTermii(String phoneNumber);

    boolean verifyOtp(String otp, String token, UserService userService) throws MessagingException;


    boolean verifyOtpWithTermii(String pin, String pinID);

}

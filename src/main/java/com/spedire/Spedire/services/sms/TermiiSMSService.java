package com.spedire.Spedire.services.sms;

import com.spedire.Spedire.services.otp.OtpService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

@Service
@Primary
public class TermiiSMSService implements SMSService {

    private OtpService otpService;

    @Override
    public String sendVerificationSMS(String phoneNumber) {
        return null;
    }

    @Override
    public boolean checkVerificationCode(String verificationCode, String token) {
        return false;
    }

    @Override
    public String sendSMSWithTermii(String phoneNumber) {
//        var otp = otpService.generateOtp(phoneNumber);
//        Unirest.setTimeouts(0, 0);
//        HttpResponse<String> response = Unirest.post("https://api.ng.termii.com/api/sms/send")
//                .header("Content-Type", "application/json")
//                .body("{\r\n \"to\":\"2347880234567\",\r\n \"from\":\"Spedire\",\r\n  \"sms\":\"OTP: {otp.getCode();}\",\r\n \"type\":\"plain\",\r\n  \"api_key\":\"TLiKdPtAAzsr4DQSGLJcsMB5X5jsikGcQkzSsSREW3zK2nWBze9Fb4f4Oj6FMF\",\r\n  \"channel\":\"generic\",\r\n;")
//                .asString();
        return null;
    }
}

package com.spedire.Spedire.services.sms;

public interface SMSService {

    String sendVerificationSMS(String phoneNumber);

    boolean checkVerificationCode(String verificationCode, String token);


}

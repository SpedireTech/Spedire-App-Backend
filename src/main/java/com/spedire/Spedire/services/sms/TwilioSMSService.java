package com.spedire.Spedire.services.sms;

import com.spedire.Spedire.services.user.UserServiceUtils;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.spedire.Spedire.services.sms.SMSUtils.*;

@Service
@AllArgsConstructor
public class TwilioSMSService implements SMSService {

    private UserServiceUtils utils;

    @Value(TWILIO_ACCOUNT_SID)
    private String accountSid;

    @Value(TWILIO_AUTH_TOKEN)
    private String authToken;

    @Value(VERIFY_SERVICE_SID)
    private String serviceSid;

    private static final Logger logger = LoggerFactory.getLogger(TwilioSMSService.class);



    @PostConstruct
    private void initializeTwilio() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public String sendVerificationSMS(String phoneNumber) {
        String removeFirstDigit = phoneNumber.substring(1, 11);
        System.out.println(removeFirstDigit);
        try {
            Verification verification = Verification.creator(
                            serviceSid,
                            PHONE_NUMBER_PREFIX+removeFirstDigit,
                            "sms").create();
            logger.info("Verification Status: {}", verification.getStatus());
            return "OTP Sent";
        } catch (ApiException exception) {
            logger.error("Error sending SMS: {}", exception.getMessage(), exception);
            return "Error Sending OTP";
        }
    }


    @Override
    public boolean checkVerificationCode(String phoneNumber, String verificationCode) {
        String removeFirstDigit = phoneNumber.substring(1, 11);
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(
                            "VA294777848da155099df6502ef7df6fa6")
                    .setTo(PHONE_NUMBER_PREFIX+removeFirstDigit)
                    .setCode(verificationCode)
                    .create();
            var status = "approved".equals(verificationCheck.getStatus());
            if (status) utils.setOtpVerificationStatusToTrue(phoneNumber);
            return status;
        } catch (ApiException exception) {
            logger.error("Error checking verification code: {}", exception.getMessage(), exception);
            return false;
        }
    }


}
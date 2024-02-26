package com.spedire.Spedire.services.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class SMSUtils {

    @Value("${twilio.accountSid}")
    public String TWILIO_ACCOUNT_SID;

    @Value("${twilio.authToken}")
    public String TWILIO_AUTH_TOKEN;

    @Value("${twilio.verifySid}")
    public String VERIFY_SERVICE_SID;

    public static final String PHONE_NUMBER_PREFIX = "+234";


}

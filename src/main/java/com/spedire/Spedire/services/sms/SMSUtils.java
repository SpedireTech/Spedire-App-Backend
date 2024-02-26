package com.spedire.Spedire.services.sms;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class SMSUtils {

    public static final String TWILIO_ACCOUNT_SID = "${twilio.accountSid}";

    public static final String TWILIO_AUTH_TOKEN = "${twilio.authToken}";

//    public static final String TWILIO_NUMBER = "${twilio.number}";
    public static final String VERIFY_SERVICE_SID = "${twilio.verifySid}";

    public static final String PHONE_NUMBER_PREFIX="+234";


}

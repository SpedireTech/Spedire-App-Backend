package com.spedire.Spedire.services.sms;

import org.springframework.stereotype.Component;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Component
public class SpedireSMSService implements SMSService {

    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    @Override
    public String sendOTPToPhoneNumber(String phoneNumber) {
        // Find your Account SID and Auth Token at twilio.com/console
        // and set the environment variables. See http://twil.io/secure
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                       new com.twilio.type.PhoneNumber("+15558675310"),
                       new com.twilio.type.PhoneNumber("+15017122661"),
                      "This is the ship that made the Kessel Run in fourteen parsecs?")
                .create();

        System.out.println(message.getSid());
        return message.getSid();
    }
}

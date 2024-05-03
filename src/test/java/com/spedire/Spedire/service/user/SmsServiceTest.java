//package com.spedire.Spedire.service.user;
//
//
//import com.spedire.Spedire.services.sms.SMSService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@Slf4j
//public class SmsServiceTest {
//
//    @Autowired
//    private SMSService smsService;
//
//
//    @Test
//    public void sendSMSToPhoneNumber() {
//        var response = smsService.sendVerificationSMS("08105029925");
//        assertThat(response).isEqualTo("OTP Sent");
//    }
//
//
//    @Test
//    public void verifyOtp() {
//        var response = smsService.checkVerificationCode("08030669508", "569586");
//        assertTrue(response);
//    }
//}

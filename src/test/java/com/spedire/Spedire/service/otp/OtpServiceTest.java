package com.spedire.Spedire.service.otp;

import com.spedire.Spedire.services.otp.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.mongodb.assertions.Assertions.assertFalse;
import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.bson.assertions.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
public class OtpServiceTest {

    @Autowired
    private OtpService otpService;

    String token;

    String otp;

    @Test
    public void generateOtp(){
        var response = otpService.generateOtp("07087655443");
        assertNotNull(response.getOtpNumber());
//        token = response.getData();
        otp = response.getOtpNumber();
    }

    @Test
    public void verifyOtp(){
        var response = otpService.verifyOtp(otp, "", null);
        assertTrue(response);
    }

    @Test
    public void tokenIsInvalidAfter5minutes(){
        var response = otpService.verifyOtp(otp, "", null);
        assertFalse(response);
    }
}

package com.spedire.Spedire.service;

import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    public void userReceivesOtpViaTextMessageTest() {
        User user = new User("08023677114");
        VerifyPhoneNumberResponse response = userService.verifyUserPhoneNumberFirst(user.getPhoneNumber());
        assertThat(response.getMessage()).isEqualTo("OTP sent, Check your phone");
    }

    @Test
    public void inValidPhoneNumberThrowsExceptionTest() {
        User user = new User("06023677114");
        SpedireException exception = assertThrows(SpedireException.class, () -> {
            userService.verifyUserPhoneNumberFirst(user.getPhoneNumber());
        });
        assertEquals("Phone number is Invalid", exception.getMessage());
    }


}

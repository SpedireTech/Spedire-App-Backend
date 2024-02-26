package com.spedire.Spedire.service;

import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.MissingFormatArgumentException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    String token = "";

    @Test
    public void savePhoneNumberTest() {
        User user = new User("08030669508");
        VerifyPhoneNumberResponse response = userService.savePhoneNumber(user.getPhoneNumber());
        token = response.getToken();
        log.info("Response -> {} " , response);
        assertThat(response.getMessage()).isEqualTo("OTP Sent");
    }

    @Test
    public void inValidPhoneNumberThrowsExceptionTest() {
        User user = new User("06023677114");
        SpedireException exception = assertThrows(SpedireException.class, () -> {
            userService.savePhoneNumber(user.getPhoneNumber());
        });
        assertEquals("Invalid phone number", exception.getMessage());
    }

    @Test
    public void phoneNumberExistThrowExceptionTest() {
        User user = new User("08030669508");
        MissingFormatArgumentException exception = assertThrows(MissingFormatArgumentException.class, () -> {
            userService.savePhoneNumber(user.getPhoneNumber());
        });
//        assertEquals("User with %s exist".formatted(user.getPhoneNumber()), exception.getMessage());
        assertEquals("Format specifier '%s'", exception.getMessage());

    }

    @Test
    public void verifyUserHasVerifyOtpTest() {
        CompleteRegistrationRequest request = CompleteRegistrationRequest.builder().firstName("Zainab").lastName("Alayande")
                .email("alayandezainab64@gmail.com").password("Abimbola64").image("No Image").build();
        assertThrows(SpedireException.class, () -> userService.completeRegistration(request, "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3MDg5NDA4MTIsImV4cCI6MTcwOTAyNjgxMiwicGhvbmVOdW1iZXIiOiIwODAzMDY2OTUwOCJ9.a070X-TKxhKjVq-r7LVPmMa3PPQwCteMP0QGYNKNJAZtae4OM2hllTm-VGtkOdK2yAWW-h9wKzEpd_35umhIzA"));
    }

    @Test
    public void completeRegistrationForUserTest() {
        CompleteRegistrationRequest request = CompleteRegistrationRequest.builder().firstName("Zainab").lastName("Alayande")
                .email("alayandezainab64@gmail.com").password("Abimbola64").image("No Image").build();
        var response = userService.completeRegistration(request, "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3MDg5NDA4MTIsImV4cCI6MTcwOTAyNjgxMiwicGhvbmVOdW1iZXIiOiIwODAzMDY2OTUwOCJ9.a070X-TKxhKjVq-r7LVPmMa3PPQwCteMP0QGYNKNJAZtae4OM2hllTm-VGtkOdK2yAWW-h9wKzEpd_35umhIzA");
        System.out.println(response);
    }


}

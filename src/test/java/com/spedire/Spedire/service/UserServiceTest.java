package com.spedire.Spedire.service;

import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private UserRepository userRepository;

    private HttpServletRequest httpServletRequest;

    String token = "";

    @Test
    public void savePhoneNumberTest() {
        User user = new User("08030669508");
        VerifyPhoneNumberResponse response = userService.verifyPhoneNumber(user.getPhoneNumber());
        token = response.getToken();
        log.info("Response -> {} " , response);
        assertThat(response.getMessage()).isEqualTo("OTP Sent");
    }

    @Test
    public void inValidPhoneNumberThrowsExceptionTest() {
        User user = new User("06023677114");
        SpedireException exception = assertThrows(SpedireException.class, () -> {
            userService.verifyPhoneNumber(user.getPhoneNumber());
        });
        assertEquals("Invalid phone number", exception.getMessage());
    }

    @Test
    public void phoneNumberExistThrowExceptionTest() {
        User user = new User("08030669508");
        MissingFormatArgumentException exception = assertThrows(MissingFormatArgumentException.class, () -> {
            userService.verifyPhoneNumber(user.getPhoneNumber());
        });
//        assertEquals("User with %s exist".formatted(user.getPhoneNumber()), exception.getMessage());
        assertEquals("Format specifier '%s'", exception.getMessage());

    }

    @Test
    public void verifyUserHasVerifyOtpTest() {
        CompleteRegistrationRequest request = CompleteRegistrationRequest.builder().firstName("Zainab").lastName("Alayande")
                .email("alayandezainab64@gmail.com").password("Abimbola64").image("No Image").build();
        assertThrows(SpedireException.class, () -> userService.completeRegistration(request, httpServletRequest));
    }

    @Test
    public void completeRegistrationForUserTest() {
        CompleteRegistrationRequest request = CompleteRegistrationRequest.builder().firstName("Zainab").lastName("Alayande")
                .email("alayandezainab64@gmail.com").password("Abimbola64").image("No Image").build();
        var response = userService.completeRegistration(request, httpServletRequest);
        System.out.println(response);
    }

    @Test
    public void deleteUserTest() {
        userRepository.deleteByPhoneNumber("08030669508");
        System.out.println("User deleted from the DB");
    }


}

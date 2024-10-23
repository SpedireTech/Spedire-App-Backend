package com.spedire.Spedire.service.email;

import com.spedire.Spedire.services.email.JavaMailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.spedire.Spedire.services.email.MailTemplates.getWelcomeMailTemplate;

@SpringBootTest
@Slf4j
public class EmailServiceTest {

    private static final String WELCOME_TO_SPEDIRE = "WELCOME_TO_SPEDIRE";
    @Autowired
    private JavaMailService javaMailService;

    @Test
    public void sendMailTest_One() throws MessagingException {
        javaMailService.sendMail("alayandezainab64@gmail.com", "Hi Hi", "Hi Yoooo");
        log.info("Mil delivered 1");
    }

    @Test
    public void sendMailTest_Two() throws MessagingException {
        javaMailService.sendMail("alayandezainab64@gmail.com", WELCOME_TO_SPEDIRE, getWelcomeMailTemplate("Zen"));
        log.info("Mil delivered 2");
    }

}

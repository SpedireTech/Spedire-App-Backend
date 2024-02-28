package com.spedire.Spedire.service;

import com.spedire.Spedire.services.email.JavaMailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.spedire.Spedire.services.email.MailTemplates.getEmailTemplate;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SendMailTest {

    @Autowired
    private JavaMailService javaMailService;


    @Test
    public void sendMailTest() throws MessagingException {
        boolean status =javaMailService.sendMail("zainabalayande01@gmail.com", "Welcome to My Space",
                getEmailTemplate());
        assertTrue(status);
    }


}

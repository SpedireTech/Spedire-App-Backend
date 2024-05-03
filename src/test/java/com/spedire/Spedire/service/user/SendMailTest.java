//package com.spedire.Spedire.service.user;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.spedire.Spedire.services.email.JavaMailService;
//import jakarta.mail.MessagingException;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.Instant;
//
//import static com.spedire.Spedire.services.email.MailTemplates.getForgotPasswordMailTemplate;
//import static com.spedire.Spedire.services.email.MailTemplates.getWelcomeMailTemplate;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//public class SendMailTest {
//
//    @Autowired
//    private JavaMailService javaMailService;
//
//
//
//    @Test
//    public void sendMailTest() throws MessagingException {
//        boolean status =javaMailService.sendMail("zainabalayande01@gmail.com", "Welcome to My Space",
//                "This is a confirmation that mail is sent");
//        assertTrue(status);
//    }
//
//    @Test
//    public void welcomeMailTest() throws MessagingException {
//        boolean status =javaMailService.sendMail("zainabalayande01@gmail.com", "Welcome to My Space",
//                getWelcomeMailTemplate("Zainab Alayande"));
//        assertTrue(status);
//    }
//
//    @Test
//    public void forgotPasswordMailTest() throws MessagingException {
//        boolean status =javaMailService.sendMail("zainabalayande01@gmail.com", "Forgot Password",
//                getForgotPasswordMailTemplate("Zainab Alayande", generateResetLink("aloma@gmail.com")));
//        assertTrue(status);
//    }
//
//    public String generateResetLink(String emailAddress) {
//        String token = JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
//                .withClaim("email", emailAddress)
//                .sign(Algorithm.HMAC512("".getBytes()));
//        return "http://localhost:3000/resetPassword?token=" + token;
//    }
//
//
//}

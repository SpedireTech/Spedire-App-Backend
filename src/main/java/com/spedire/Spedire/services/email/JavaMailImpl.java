package com.spedire.Spedire.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.spedire.Spedire.services.email.EmailUtils.APP_EMAIL;


@Component
@Slf4j
public class JavaMailImpl implements JavaMailService {

    @Autowired
    private JavaMailSender emailSender;

   public boolean sendMail(String to, String subject, String text)  {
        try{
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("spediretech@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            emailSender.send(mimeMessage);
            return true;
        } catch (Exception exception){
            log.info("exception message in the catch block<--> " + exception.getMessage());
            return false;
        }
    }



    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
       try {
           MimeMessage message = emailSender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(message, true);
           helper.setFrom(APP_EMAIL);
           helper.setTo(to);
           helper.setSubject(subject);
           helper.setText(text);

           FileSystemResource file
                   = new FileSystemResource(new File(pathToAttachment));
           helper.addAttachment("Invoice", file);

           emailSender.send(message);

       } catch (MessagingException exception) {
           System.out.println(exception.getMessage());
       }

    }


}

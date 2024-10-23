package com.spedire.Spedire.services.email;

import com.spedire.Spedire.exceptions.SpedireException;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static com.spedire.Spedire.services.email.EmailUtils.*;

public class MailTemplates {

    public static String getWelcomeMailTemplate(String name) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("static/welcome.jsp").getInputStream()))) {
            String template = bufferedReader.lines().collect(Collectors.joining());
            template = template.replace("{name}", name);
            return template;
        } catch (IOException exception) {
            throw new SpedireException("Fail to send mail");
        }
    }

    public static String getForgotPasswordMailTemplate(String name, String token)  {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("static/forgotPassword.jsp").getInputStream()))) {
            String template = bufferedReader.lines().collect(Collectors.joining());

            template = template.replace("{name}", name);
            template = template.replace("{link}", "<a href='" + token + "'>Reset Password</a>");

            return template;
        } catch (IOException exception) {
            throw new SpedireException("Fail to send mail");
        }
    }


    @SneakyThrows
    public static String getSelectCourierMailTemplate(String link)  {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource("static/selectCourier.jsp").getInputStream()))) {
            String template = bufferedReader.lines().collect(Collectors.joining());

            template = template.replace("{link}", "<a href='" + link + "'>Please Login Here</a>");

            return template;
        } catch (IOException exception) {
            throw new SpedireException("Fail to send mail");
        }
    }



}

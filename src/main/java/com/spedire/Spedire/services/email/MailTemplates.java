package com.spedire.Spedire.services.email;

import com.spedire.Spedire.exceptions.SpedireException;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static com.spedire.Spedire.services.email.EmailUtils.*;

public class MailTemplates {


    @SneakyThrows
    public static String getWelcomeMailTemplate(String name)  {
        try(BufferedReader bufferedReader =
                    new BufferedReader(new FileReader(WELCOME_MAIL_TEMPLATE_LOCATION))) {
            String template = bufferedReader.lines().collect(Collectors.joining());
            template = template.replace("{name}", name);
            return template;
        } catch (IOException exception) {
            throw new SpedireException("Failed to send mail");
        }
    }

    @SneakyThrows
    public static String getForgotPasswordMailTemplate(String name, String token)  {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FORGOT_PASSWORD_MAIL_TEMPLATE_LOCATION))) {
            String template = bufferedReader.lines().collect(Collectors.joining());

            template = template.replace("{name}", name);
            template = template.replace("{link}", "<a href='" + token + "'>Reset Password</a>");

            return template;
        } catch (IOException exception) {
            throw new SpedireException("Failed to send mail");
        }
    }


    @SneakyThrows
    public static String getSelectCourierMailTemplate(String link)  {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(SELECT_COURIER_MAIL_TEMPLATE_LOCATION))) {
            String template = bufferedReader.lines().collect(Collectors.joining());

            template = template.replace("{link}", "<a href='" + link + "'>Please Login Here</a>");

            return template;
        } catch (IOException exception) {
            throw new SpedireException("Failed to send mail");
        }
    }



}

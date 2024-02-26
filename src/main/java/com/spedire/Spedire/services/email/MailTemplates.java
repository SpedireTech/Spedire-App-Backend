package com.spedire.Spedire.services.email;

import com.spedire.Spedire.exceptions.SpedireException;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static com.spedire.Spedire.services.email.EmailUtils.MAIL_TEMPLATE_LOCATION;

public class MailTemplates {


    @SneakyThrows
    public static String getEmailTemplate()  {
        try(BufferedReader bufferedReader =
                    new BufferedReader(new FileReader(MAIL_TEMPLATE_LOCATION))) {
            return bufferedReader.lines().collect(Collectors.joining());
        } catch (IOException exception) {
            throw new SpedireException("Failed to send mail");
        }
    }
}

package com.spedire.Spedire.services.user;

import com.spedire.Spedire.exceptions.SpedireException;
import lombok.SneakyThrows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserServiceUtils {

    private static final String EMAIL_REGEX_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String PHONE_NUMBER_REGEX = "^(080|091|070|081|090)\\d{8}$";

    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);

    @SneakyThrows
    public static void verifyPhoneNumberIsValid(String phoneNumber) {
        if (phoneNumber == null) {
            throw new SpedireException("Phone number is null");
        }

        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()){
            throw new SpedireException("Phone number is Invalid");
        }

    }
}

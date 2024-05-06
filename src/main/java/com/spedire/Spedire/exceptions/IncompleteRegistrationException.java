package com.spedire.Spedire.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class IncompleteRegistrationException extends RuntimeException {

    public IncompleteRegistrationException(String message) {
        super(message);
    }

}

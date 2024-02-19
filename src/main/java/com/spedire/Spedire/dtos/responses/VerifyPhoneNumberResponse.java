package com.spedire.Spedire.dtos.responses;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Builder
public class VerifyPhoneNumberResponse {

    private String message;
}

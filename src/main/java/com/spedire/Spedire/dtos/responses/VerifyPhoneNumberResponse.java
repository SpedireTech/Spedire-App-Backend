package com.spedire.Spedire.dtos.responses;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class VerifyPhoneNumberResponse {

    private String message;
    private String token;

}

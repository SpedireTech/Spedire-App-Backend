package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class VerifyPhoneNumberResponse {

    private String otp;
    private String token;

}

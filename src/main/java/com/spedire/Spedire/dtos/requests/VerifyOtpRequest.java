package com.spedire.Spedire.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyOtpRequest {

    String phoneNumber;
    String verificationCode;
}

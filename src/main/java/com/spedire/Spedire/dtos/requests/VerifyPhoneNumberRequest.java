package com.spedire.Spedire.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyPhoneNumberRequest {

    private boolean route;
    private String phoneNumber;
}
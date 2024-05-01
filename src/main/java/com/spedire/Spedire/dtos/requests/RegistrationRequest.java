package com.spedire.Spedire.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class RegistrationRequest {

    private String fullName;
    private String email;
    private String password;

}

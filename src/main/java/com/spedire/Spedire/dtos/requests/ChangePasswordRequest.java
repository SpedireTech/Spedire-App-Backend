package com.spedire.Spedire.dtos.requests;

import lombok.Getter;

@Getter
public class ChangePasswordRequest {

    private String newPassword;
    private String confirmPassword;
    private String token;

}

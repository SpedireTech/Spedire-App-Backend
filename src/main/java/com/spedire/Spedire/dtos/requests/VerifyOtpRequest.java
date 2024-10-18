package com.spedire.Spedire.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyOtpRequest {

    String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getPin_id() {
        return pin_id;
    }

    public void setPin_id(String pin_id) {
        this.pin_id = pin_id;
    }

    String pin_id;
}

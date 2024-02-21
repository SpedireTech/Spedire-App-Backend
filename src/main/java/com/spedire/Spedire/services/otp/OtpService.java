package com.spedire.Spedire.services.otp;


import com.spedire.Spedire.dtos.responses.OtpResponse;
import com.spedire.Spedire.exceptions.OtpException;
import com.spedire.Spedire.exceptions.PhoneNumberNotVerifiedException;
import com.spedire.Spedire.models.Otp;

public interface OtpService {

    OtpResponse createNewOtp(String phoneNumber);
    void deleteOtp(String phoneNumber);
    OtpResponse findByPhoneNumber(String phoneNumber) throws PhoneNumberNotVerifiedException;

    Otp findByOtp(String otpNumber) throws OtpException;

}

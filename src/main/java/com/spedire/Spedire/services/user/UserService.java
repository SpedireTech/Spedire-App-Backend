package com.spedire.Spedire.services.user;

import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.requests.ForgotPasswordRequest;
import com.spedire.Spedire.dtos.responses.ChangePasswordResponse;
import com.spedire.Spedire.dtos.responses.CompleteRegistrationResponse;
import com.spedire.Spedire.dtos.responses.ForgotPasswordResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    VerifyPhoneNumberResponse verifyPhoneNumber(String phoneNumber);

    CompleteRegistrationResponse completeRegistration(CompleteRegistrationRequest request, HttpServletRequest httpServletRequest);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws SpedireException;

    ChangePasswordResponse resetPassword(ChangePasswordRequest passwordResetRequest) throws SpedireException;


}

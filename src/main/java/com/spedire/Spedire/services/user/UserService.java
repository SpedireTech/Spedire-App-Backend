package com.spedire.Spedire.services.user;

import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.requests.ForgotPasswordRequest;
import com.spedire.Spedire.dtos.requests.RegistrationRequest;
import com.spedire.Spedire.dtos.responses.*;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    RegistrationResponse createUser(RegistrationRequest request);

    VerifyPhoneNumberResponse verifyPhoneNumber(HttpServletRequest request, boolean route, String phoneNumber);

    UserProfileResponse fetchUserProfile(String token);
//    void completeRegistration(String token);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws SpedireException;

    ChangePasswordResponse resetPassword(ChangePasswordRequest passwordResetRequest) throws SpedireException;

    void saveUser(String token) throws MessagingException;

}

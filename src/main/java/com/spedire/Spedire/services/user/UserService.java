package com.spedire.Spedire.services.user;

import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.ForgotPasswordRequest;
import com.spedire.Spedire.dtos.requests.RegistrationRequest;
import com.spedire.Spedire.dtos.requests.VerifyPhoneNumberRequest;
import com.spedire.Spedire.dtos.responses.*;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface UserService {

    RegistrationResponse createUser(RegistrationRequest request);

    VerifyPhoneNumberResponse<?> verifyPhoneNumber(HttpServletRequest httpServletRequest, VerifyPhoneNumberRequest request) throws MessagingException;

    UserDashboardResponse fetchDashboardInfoForUser(String token);

    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws SpedireException;

    ChangePasswordResponse resetPassword(ChangePasswordRequest passwordResetRequest) throws SpedireException, MessagingException;

    User  saveUser(User user) throws MessagingException;

    void deliveryStatus(boolean status, String token);

    Optional<User> findByEmail(String email);

    User findById(String senderId);


    void save(User user);

}

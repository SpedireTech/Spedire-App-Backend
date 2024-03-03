package com.spedire.Spedire.services.user;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.requests.ForgotPasswordRequest;
import com.spedire.Spedire.dtos.responses.ChangePasswordResponse;
import com.spedire.Spedire.dtos.responses.CompleteRegistrationResponse;
import com.spedire.Spedire.dtos.responses.ForgotPasswordResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.sms.SMSService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static com.spedire.Spedire.services.email.MailTemplates.*;
import static com.spedire.Spedire.services.user.UserServiceUtils.*;

@AllArgsConstructor
@Service
@Slf4j
public class SpedireUserService implements UserService{

    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SMSService smsService;

    private UserServiceUtils utils;

    @Override
    @Transactional
    public VerifyPhoneNumberResponse verifyPhoneNumber(String phoneNumber) {
        verifyPhoneNumberIsValid(phoneNumber);
        validatePhoneNumberDoesntExist(phoneNumber, userRepository);
        User user = new User(phoneNumber);
        String message = smsService.sendVerificationSMS(phoneNumber);
        return utils.getVerifyPhoneNumberResponse(user, message);
    }


    @SneakyThrows
    @Override
    @Transactional
    public CompleteRegistrationResponse completeRegistration(CompleteRegistrationRequest request, HttpServletRequest httpServletRequest) {
        System.out.println("HttpServletRequest => " + httpServletRequest.toString());
        String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("authorizationHeader => " + authorizationHeader);
        String extractedToken = extractTokenFromAuthorizationHeader(authorizationHeader);
        System.out.println("extractedToken -> " + extractedToken);
        String phoneNumber = utils.decodeToken(extractedToken);
        utils.checkIfUserHasVerifiedOtp(phoneNumber);
        Optional<User> foundUser = userRepository.findByPhoneNumber(phoneNumber);
        if (foundUser.isEmpty()) {
            throw new SpedireException("User not found for phone number: " + phoneNumber);
        }
        validateEmailAddress(request.getEmail());
        User savedUser = userRepository.save(utils.buildRegistrationRequest(request, foundUser.get()));
        String response = utils.sendEmail(savedUser.getEmail(), "Welcome to Spedire", getWelcomeMailTemplate(savedUser.getFirstName()));

        if (savedUser.getId() != null && "Mail delivered successfully".equals(response)) {
            return CompleteRegistrationResponse.builder().message("USER_REGISTRATION_SUCCESSFUL").success(true).build();
        } else {
            return CompleteRegistrationResponse.builder().message("Failed to deliver email").success(false).build();
        }
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String emailAddress = forgotPasswordRequest.getEmail();
        validateEmailAddress(emailAddress);
        Optional<User> user = userRepository.findByEmail(emailAddress);
        if (user.isPresent()) {
            String link = utils.generateResetLink(emailAddress);
            System.out.println("The link here ==> " + link);
            String name = user.get().getFirstName() + user.get().getLastName();
            String message = utils.sendEmail(emailAddress, "Password Reset", getForgotPasswordMailTemplate(name, link));
            if ("Mail delivered successfully".equals(message)) return ForgotPasswordResponse.builder().status(true).message(String.format("Reset instructions sent to %s", emailAddress)).build();
        }
        return ForgotPasswordResponse.builder().status(false).message("Email Address Not Found").build();
    }

    @Override
    @SneakyThrows
    public ChangePasswordResponse resetPassword(ChangePasswordRequest passwordResetRequest)  {
        String token = passwordResetRequest.getToken();
        String newPassword = passwordResetRequest.getNewPassword();
        validatePasswordMatch(passwordResetRequest);
        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        Claim claim = decodedJWT.getClaim("email");
        String email = claim.asString();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new SpedireException(String.format("%s not found", email)));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ChangePasswordResponse.builder().status(true).message("Your password has been reset").build();
    }

}

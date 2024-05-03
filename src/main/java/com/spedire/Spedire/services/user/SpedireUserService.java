package com.spedire.Spedire.services.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.requests.ForgotPasswordRequest;
import com.spedire.Spedire.dtos.requests.RegistrationRequest;
import com.spedire.Spedire.dtos.responses.*;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.otp.OtpService;
import com.spedire.Spedire.services.sms.SMSService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;
import static com.spedire.Spedire.services.email.MailTemplates.*;
import static com.spedire.Spedire.services.user.UserServiceUtils.*;


@Service
@Slf4j
public class SpedireUserService implements UserService{

    public SpedireUserService(UserRepository userRepository, OtpService otpService,
                              PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserServiceUtils utils, JavaMailService javaMailService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.utils = utils;
        this.javaMailService = javaMailService;
    }

    @Value(JWT_SECRET)
    private String secret;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailService javaMailService;
//    private final SMSService smsService;

    private final UserServiceUtils utils;


    @Override
    public RegistrationResponse createUser(RegistrationRequest registrationRequest) {
        verifyPhoneNumberIsValid(registrationRequest.getPhoneNumber());
        validatePhoneNumberDoesntExist(registrationRequest.getPhoneNumber(), userRepository);
        validateEmailAddress(registrationRequest.getEmail());
//        User user = new User();
//        user.setEmail(registrationRequest.getEmail());
//        user.setFullName(registrationRequest.getFullName());
//        user.setPassword(registrationRequest.getPassword());
//        user.setPhoneNumber(registrationRequest.getPhoneNumber());

        String token = JWT.create()
                .withIssuedAt(Instant.now())
                .withClaim("password", registrationRequest.getPassword())
                .withClaim("email", registrationRequest.getEmail())
                .withClaim("fullName", registrationRequest.getFullName())
                .withClaim("phoneNumber", registrationRequest.getPhoneNumber())
                .sign(Algorithm.HMAC512(secret.getBytes()));
        VerifyPhoneNumberResponse response = verifyPhoneNumber(null, false, registrationRequest.getPhoneNumber());
        return RegistrationResponse.builder().token(token).otp(response.getOtp()).build();
    }

    @Override
    @Transactional
    public VerifyPhoneNumberResponse verifyPhoneNumber(HttpServletRequest request, boolean route, String phoneNumber) {
        verifyPhoneNumberIsValid(phoneNumber);
        validatePhoneNumberDoesntExist(phoneNumber, userRepository);
        if (route) {
            String authorizationHeader = request.getHeader("Authorization");
            DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
            String token = utils.generateFreshTokenWhereOAuthIsTrue(decodedJWT, phoneNumber);
            OtpResponse otp = otpService.generateOtp(phoneNumber);
            return utils.getVerifyPhoneNumberResponse(token, otp.getOtpNumber());
        } else {
//            String authorizationHeader = request.getHeader("Authorization");
//            DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
//            String token = utils.generateFreshTokenWhereOAuthIsFalse(phoneNumber);
            OtpResponse otp = otpService.generateOtp(phoneNumber);
            return utils.getVerifyPhoneNumberResponse("", otp.getOtpNumber());
        }
    }

    @Override
    public UserProfileResponse fetchUserProfile(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim("email").asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException("Invalid email address"));
        return UserProfileResponse.builder().email(user.getEmail()).phoneNumber(user.getPhoneNumber()).fullName(user.getFullName()).build();
    }


    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String emailAddress = forgotPasswordRequest.getEmail();
        validateEmailAddress(emailAddress);
        Optional<User> user = userRepository.findByEmail(emailAddress);
        if (user.isPresent()) {
            String link = utils.generateResetLink(emailAddress);
            log.info("Password Reset link : {} " + link);
            String name = user.get().getFullName();
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

    @SneakyThrows
    @Override
    public void saveUser(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim("email").asString();
        String image = decodedJWT.getClaim("image").asString();
        String phoneNumber = decodedJWT.getClaim("phoneNumber").asString();
        String password = decodedJWT.getClaim("password").asString();
        String fullName = decodedJWT.getClaim("fullName").asString();

        User savedUser;
        if (password == null) {
            User user = User.builder().fullName(fullName).phoneNumber(phoneNumber).email(email)
                    .profileImage(image).otpVerificationStatus(true).createdAt(LocalDateTime.now()).build();
            savedUser = userRepository.save(user);
        } else {
            User user = User.builder().fullName(fullName).password(passwordEncoder.encode(password)).phoneNumber(phoneNumber).email(email)
                    .profileImage(image).otpVerificationStatus(true).createdAt(LocalDateTime.now()).build();
            savedUser = userRepository.save(user);
        }

        javaMailService.sendMail(savedUser.getEmail(), "Welcome to Spedire", getWelcomeMailTemplate(savedUser.getFullName()));
    }

}

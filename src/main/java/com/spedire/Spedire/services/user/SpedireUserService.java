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
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setFullName(registrationRequest.getFullName());
        user.setPassword(registrationRequest.getPassword());
        String token = JWT.create()
                .withIssuedAt(Instant.now())
                .withClaim("password", user.getPassword())
                .withClaim("email", user.getEmail())
                .withClaim("fullName", user.getFullName())
                .sign(Algorithm.HMAC512(secret.getBytes()));
        System.out.println("service token == " + token);
        return RegistrationResponse.builder().token(token).build();
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
            String authorizationHeader = request.getHeader("Authorization");
            DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
            String token = utils.generateFreshTokenWhereOAuthIsFalse(decodedJWT, phoneNumber);
            OtpResponse otp = otpService.generateOtp(phoneNumber);
            return utils.getVerifyPhoneNumberResponse(token, otp.getOtpNumber());
        }
    }

    @Override
    public UserProfileResponse fetchUserProfile(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim("email").asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException("Invalid email address"));
        return UserProfileResponse.builder().profileImage(user.getEmail()).phoneNumber(user.getPhoneNumber()).fullName(user.getFullName()).build();
    }


//    @SneakyThrows
//    @Override
//    @Transactional
//    public void completeRegistration(String token) {
//        DecodedJWT decodeToken = utils.decodeToken(token);
//        String phoneNumber = decodeToken.getClaim("phoneNumber").asString();
//        String email = decodeToken.getClaim("email").asString();
//        String image = decodeToken.getClaim("image").asString();
//        String fullName = decodeToken.getClaim("fullName").asString();
//        String password = decodeToken.getClaim("password").asString();
//        CompleteRegistrationRequest request = CompleteRegistrationRequest.builder().image(image).fullName(fullName).email(email).password(password).phoneNumber(phoneNumber).build();
//        utils.checkIfUserHasVerifiedOtp(phoneNumber);
//        validateEmailAddress(email);
//        User savedUser = userRepository.save(utils.buildRegistrationRequest(request));
//        utils.sendEmail(savedUser.getEmail(), "Welcome to Spedire", getWelcomeMailTemplate(savedUser.getFirstName()));
//    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String emailAddress = forgotPasswordRequest.getEmail();
        validateEmailAddress(emailAddress);
        Optional<User> user = userRepository.findByEmail(emailAddress);
        if (user.isPresent()) {
            String link = utils.generateResetLink(emailAddress);
            log.info("Password Reset link : {} " + link);
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
        User user = User.builder().fullName(fullName).password(password).phoneNumber(phoneNumber).email(email)
                .profileImage(image).otpVerificationStatus(true).createdAt(LocalDateTime.now()).build();
        User savedUser = userRepository.save(user);
        javaMailService.sendMail(savedUser.getEmail(), "Welcome to Spedire", getWelcomeMailTemplate(savedUser.getFullName()));
    }

}

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
import com.spedire.Spedire.services.cache.RedisInterface;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.otp.OtpService;
import com.spedire.Spedire.services.sms.SMSService;
import jakarta.mail.MessagingException;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;
import static com.spedire.Spedire.services.email.MailTemplates.*;
import static com.spedire.Spedire.services.user.UserServiceUtils.*;
import static org.apache.http.HttpHeaders.AUTHORIZATION;


@Service
@Slf4j
public class SpedireUserService implements UserService{

    public SpedireUserService(UserRepository userRepository, OtpService otpService,
                              PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserServiceUtils utils,
                              JavaMailService javaMailService, RedisInterface redisInterface) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.utils = utils;
        this.javaMailService = javaMailService;
        this.redisInterface = redisInterface;
    }

    @Value(JWT_SECRET)
    private String secret;
    private final UserRepository userRepository;
    private final OtpService otpService;
    private PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailService javaMailService;
    private final UserServiceUtils utils;
    private final RedisInterface redisInterface;



    @Override
    public RegistrationResponse createUser(RegistrationRequest registrationRequest) {
        validateRequest(registrationRequest);
        boolean exists = redisInterface.isUserExist(registrationRequest.getEmail());
        User cachedUser = redisInterface.getUserData(registrationRequest.getEmail());
        String token = utils.generateToken(registrationRequest.getEmail());

        if (exists || cachedUser != null) {return RegistrationResponse.builder().token(token).build();}

        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        cacheUserData(registrationRequest, encodedPassword);
        VerifyPhoneNumberResponse response = verifyPhoneNumber(null, false, registrationRequest.getPhoneNumber());
        return RegistrationResponse.builder().token(token).otp(response.getOtp()).build();
    }

    private void cacheUserData(RegistrationRequest registrationRequest, String encodedPassword) {
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setFullName(registrationRequest.getFullName());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(registrationRequest.getPhoneNumber());
        redisInterface.cacheUserData(user);
    }

    private void validateRequest(RegistrationRequest registrationRequest) {
        verifyPhoneNumberIsValid(registrationRequest.getPhoneNumber());
        validateEmailAddress(registrationRequest.getEmail());
        validateEmailDoesntExist(registrationRequest.getEmail(), userRepository);
        validatePhoneNumberDoesntExist(registrationRequest.getPhoneNumber(), userRepository);
    }


    @Override
    @Transactional
    public VerifyPhoneNumberResponse verifyPhoneNumber(HttpServletRequest request, boolean route, String phoneNumber) {
        verifyPhoneNumberIsValid(phoneNumber);
        validatePhoneNumberDoesntExist(phoneNumber, userRepository);
        if (route) {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
            String email = decodedJWT.getClaim(EMAIL).asString();
            User user = redisInterface.getUserData(email);
            user.setPhoneNumber(phoneNumber);
            redisInterface.cacheUserData(user);
            String token = utils.generateFreshTokenWhereOAuthIsTrue(email);
            OtpResponse otp = otpService.generateOtp(phoneNumber);
            return utils.getVerifyPhoneNumberResponse(token, otp.getOtpNumber());
        } else {
            OtpResponse otp = otpService.generateOtp(phoneNumber);
            return utils.getVerifyPhoneNumberResponse("", otp.getOtpNumber());
        }
    }

    @Override
    public UserProfileResponse fetchUserProfile(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException(INVALID_EMAIL_ADDRESS));
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
            String message = utils.sendEmail(emailAddress, PASSWORD_RESET, getForgotPasswordMailTemplate(name, link));
            if (MAIL_DELIVERED_SUCCESSFULLY.equals(message)) return ForgotPasswordResponse.builder().status(true).message(String.format(RESET_INSTRUCTIONS_SENT, emailAddress)).build();
        }
        return ForgotPasswordResponse.builder().status(false).message(EMAIL_ADDRESS_NOT_FOUND).build();
    }

    @Override
    public ChangePasswordResponse resetPassword(ChangePasswordRequest passwordResetRequest)  {
        String token = passwordResetRequest.getToken();
        String newPassword = passwordResetRequest.getNewPassword();
        validatePasswordMatch(passwordResetRequest);
        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        Claim claim = decodedJWT.getClaim(EMAIL);
        String email = claim.asString();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new SpedireException(String.format(NOT_FOUND, email)));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ChangePasswordResponse.builder().status(true).message(YOUR_PASSWORD_HAS_BEEN_REST).build();
    }


    @Override
    public void saveUser(String token) throws MessagingException {
        System.out.println("hello");
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User cachedUser = redisInterface.getUserData(email);
        System.out.println(cachedUser.toString());
        User user = User.builder().fullName(cachedUser.getFullName()).password(cachedUser.getPassword())
                .phoneNumber(cachedUser.getPhoneNumber()).email(cachedUser.getEmail()).profileImage(cachedUser.getProfileImage()).otpVerificationStatus(true).createdAt(LocalDateTime.now()).build();
        User savedUser = userRepository.save(user);
        javaMailService.sendMail(savedUser.getEmail(), WELCOME_TO_SPEDIRE, getWelcomeMailTemplate(savedUser.getFullName()));
        redisInterface.deleteUserCache(email);
    }

}

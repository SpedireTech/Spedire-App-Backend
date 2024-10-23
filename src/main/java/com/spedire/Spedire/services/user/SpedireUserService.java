package com.spedire.Spedire.services.user;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.ForgotPasswordRequest;
import com.spedire.Spedire.dtos.requests.RegistrationRequest;
import com.spedire.Spedire.dtos.requests.VerifyPhoneNumberRequest;
import com.spedire.Spedire.dtos.responses.*;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.cache.RedisInterface;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.otp.OtpService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.spedire.Spedire.enums.Role.SENDER;
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

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailService javaMailService;
    private final UserServiceUtils utils;
    private final RedisInterface redisInterface;


    @Override
    public RegistrationResponse createUser(RegistrationRequest registrationRequest) {
        utils.validateRequest(registrationRequest);
        String email = registrationRequest.getEmail();
        String phoneNumber = registrationRequest.getPhoneNumber();
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        if (redisInterface.getUserData(email) == null) {
            utils.cacheUserData(registrationRequest, encodedPassword);
        }

        String token = utils.generateToken(email);
        String pinId = otpService.generateOtpWithTermii(phoneNumber);
        return RegistrationResponse.builder().token(token).pinId(pinId).build();
    }


    @Override
    @Transactional
    public VerifyPhoneNumberResponse<?> verifyPhoneNumber(HttpServletRequest httpServletRequest, VerifyPhoneNumberRequest request) throws RedisConnectionFailureException, MessagingException {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = redisInterface.getUserData(email);
        boolean response = otpService.verifyOtpWithTermii(request.getPin(), request.getPinId());
        if (response) {
            Optional<User> foundUserOptional = userRepository.findByEmail(email);
            if (foundUserOptional.isPresent()) {
                User foundUser = foundUserOptional.get();
                if (foundUser.isOtpVerificationStatus()) {
                    return VerifyPhoneNumberResponse.builder().message("Phone number already verified").build();
                }
            }
            User savedUser = saveUser(user);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("id", savedUser.getId()); data.put("name", savedUser.getFullName()); data.put("phone", savedUser.getPhoneNumber()); data.put("email", savedUser.getEmail());
            return VerifyPhoneNumberResponse.builder().message("Registration Successful").data(data).build();
        }
        return VerifyPhoneNumberResponse.builder().message("OTP Expired").build();
    }



    @Override
    public UserDashboardResponse fetchDashboardInfoForUser(String token) {

        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException(INVALID_EMAIL_ADDRESS));
        return getUserDashboardResponse(user);
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
            if (MAIL_DELIVERED_SUCCESSFULLY.equals(message)) {
                return ForgotPasswordResponse.builder().status(true).message(String.format(RESET_INSTRUCTIONS_SENT, emailAddress)).build();
            } else {
                return ForgotPasswordResponse.builder().status(false).message(MAIL_DELIVERY_FAILED).build();
            }
        }
        return ForgotPasswordResponse.builder().status(false).message(EMAIL_ADDRESS_NOT_FOUND).build();
    }


    @Override
    public ChangePasswordResponse resetPassword(ChangePasswordRequest passwordResetRequest) throws MessagingException {
        String token = passwordResetRequest.getToken();
        String newPassword = passwordResetRequest.getNewPassword();
        validatePasswordMatch(passwordResetRequest);
        validatePassword(passwordResetRequest.getNewPassword());
        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        Claim claim = decodedJWT.getClaim(EMAIL);
        String email = claim.asString();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new SpedireException(String.format(NOT_FOUND, email)));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        javaMailService.sendMail(email, "Password Reset Successful", "Password Reset Successful");
        return ChangePasswordResponse.builder().status(true).message(YOUR_PASSWORD_HAS_BEEN_REST).build();
    }


    @Override
    public User saveUser(User user) throws MessagingException {
        user = User.builder().fullName(user.getFullName()).password(user.getPassword())
                .phoneNumber(user.getPhoneNumber()).email(user.getEmail()).profileImage(user.getProfileImage())
                .otpVerificationStatus(true).roles(new HashSet<>(Set.of(Role.SENDER))).createdAt(LocalDateTime.now()).build();
        User savedUser = userRepository.save(user);
        javaMailService.sendMail(savedUser.getEmail(), WELCOME_TO_SPEDIRE, getWelcomeMailTemplate(savedUser.getFullName()));
        redisInterface.deleteUserCache(user.getEmail());
        return savedUser;
    }

    @Override
    public void deliveryStatus(boolean status, String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException(INVALID_EMAIL_ADDRESS));
        user.setOpenToDelivery(status);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findById(String senderId) {
        return userRepository.findById(senderId).orElseThrow(() -> new SpedireException("User not found"));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }



}

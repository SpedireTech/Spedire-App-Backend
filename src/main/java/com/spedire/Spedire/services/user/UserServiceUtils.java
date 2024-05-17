package com.spedire.Spedire.services.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.ChangePasswordRequest;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.UserDashboardResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.email.JavaMailService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.spedire.Spedire.enums.Role.SENDER;
import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;


@AllArgsConstructor
@Slf4j
public class UserServiceUtils {

    @Value(JWT_SECRET)
    private String secret;

    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    private JavaMailService javaMailService;

    private static final String EMAIL_REGEX_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX_PATTERN);

    private static final String PHONE_NUMBER_REGEX = "^(080|091|070|081|090)\\d{8}$";

    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
    public static final String WELCOME_TO_SPEDIRE = "Welcome to Spedire";
    public static final String EMAIL = "email";
    public static final String YOUR_PASSWORD_HAS_BEEN_REST = "Your password has been reset";
    public static final String EMAIL_ADDRESS_NOT_FOUND = "Email Address Not Found";
    public static final String NOT_FOUND = "%s not found";
    public static final String PASSWORD_RESET = "Password Reset";
    public static final String RESET_INSTRUCTIONS_SENT = "Reset instructions sent to %s";
    public static final String MAIL_DELIVERY_FAILED = "Mail delivery failed";
    public static final String MAIL_DELIVERED_SUCCESSFULLY = "Mail delivered successfully";
    public static final String INVALID_EMAIL_ADDRESS = "Invalid email address";
    public static final String INCOMPLETE_REGISTRATION = "Incomplete registration, Verify your phone number to continue";


    public static void verifyPhoneNumberIsValid(String phoneNumber) {
        if (phoneNumber == null) {
            throw new SpedireException("Phone number can not be null");
        }
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()){
            throw new SpedireException("Invalid phone number");
        }
    }


    public static void validatePhoneNumberDoesntExist(String phoneNumber, UserRepository userRepository) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new SpedireException(String.format("User with %s already exists", phoneNumber));
        }
    }

    public static void validateEmailDoesntExist(String email, UserRepository userRepository) {
        if (userRepository.existsByEmail(email)) {
            throw new SpedireException(String.format("User with %s already exists", email));
        }
    }


    public String decodePhoneNumber(String token) {
        log.info("Token : {} " , token);
        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        return decodedJWT.getClaim("phoneNumber").asString();
    }


    public DecodedJWT decodeToken(String token) {
//        String splitToken = token.split(" ")[1];
        log.info("Token : {} " , token);
        return jwtUtil.verifyToken(token);
    }


    @SneakyThrows
    protected static void validateEmailAddress(String email) {
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            throw new SpedireException("Invalid email address");
        }
    }

    public DecodedJWT extractTokenDetails(String token) {
        String splitToken = token.split(" ")[1];
        return jwtUtil.verifyToken(splitToken);
    }

    public String generateFreshTokenWhereOAuthIsTrue(String email) {
        return JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
                .withClaim("email", email)
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String generateFreshTokenWhereOAuthIsFalse(String phoneNumber) {
        return JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
                .withClaim("phoneNumber", phoneNumber)
                .sign(Algorithm.HMAC512(secret.getBytes()));

    }

    public VerifyPhoneNumberResponse getVerifyPhoneNumberResponse(String token, String otp) {
        return VerifyPhoneNumberResponse.builder().otp(otp).token(token).build();
//        if (message.equals("OTP Sent")) {
//            User storedUser = userRepository.save(filledUser);
//            String token = fetchToken(storedUser.getPhoneNumber());
//            return VerifyPhoneNumberResponse.builder().message(message).token(token).build();
//        } else {
//            return VerifyPhoneNumberResponse.builder().message(message).build();
//        }
    }

    public User buildRegistrationRequest(CompleteRegistrationRequest registrationRequest)  {
        User user = new User();
        user.getRoles().add(SENDER);
//        user.setFirstName(registrationRequest.getFirstName());
//        user.setLastName(registrationRequest.getLastName());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setProfileImage(registrationRequest.getImage());
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public String fetchToken(String phoneNumber) {
        return JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
                .withClaim("phoneNumber", phoneNumber)
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    @SneakyThrows
    public static void validatePasswordMatch(ChangePasswordRequest request) {
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) throw new IllegalArgumentException("Password Mismatch");

    }

    @SneakyThrows
    public String sendEmail(String recipientEmail, String subject, String template) {
       boolean status = javaMailService.sendMail(recipientEmail, subject, template);
       if (status) {
           return "Mail delivered successfully";
       } else return "Mail delivery failed";
    }

    @SneakyThrows
    public void setOtpVerificationStatusToTrue(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent()) {
            user.get().setOtpVerificationStatus(true);
            userRepository.save(user.get());
        } else throw new SpedireException("Phone number not found");
    }

    @SneakyThrows
    public void checkIfUserHasVerifiedOtp(String phoneNumber) {
        log.info("Phone number : {} " , phoneNumber);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent() && !user.get().isOtpVerificationStatus()) {
            throw new SpedireException("User yet to verify Otp");
        }
    }


    public static UserDashboardResponse getUserDashboardResponse(User user) {
        return UserDashboardResponse.builder().email(user.getEmail()).
                phoneNumber(user.getPhoneNumber())
                .totalPendingDelivery(String.valueOf(user.getTotalPendingDelivery()))
                .totalSuccessfulDelivery(String.valueOf(user.getTotalSuccessfulDelivery()))
                .totalSentItem(String.valueOf(user.getTotalSentItem()))
                .walletBalance("0.00")
                .openToDelivery(user.isOpenToDelivery())
                .fullName(user.getFullName())
                .totalCancelledDelivery(String.valueOf(user.getTotalCancelledDelivery())).build();
    }

    public String generateResetLink(String emailAddress) {
        String token = JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
                .withClaim("email", emailAddress)
                .sign(Algorithm.HMAC512(secret.getBytes()));
        return "http://localhost:3000/reset-password?token=" + token;
    }


    public static String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public String generateToken(String email) {
       return JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
                .withClaim(EMAIL, email).sign(Algorithm.HMAC512(secret.getBytes()));
    }

}

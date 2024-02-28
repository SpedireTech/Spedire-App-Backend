package com.spedire.Spedire.services.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.email.JavaMailService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.spedire.Spedire.enums.Role.SENDER;
import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;
import static com.spedire.Spedire.services.email.MailTemplates.getEmailTemplate;


@AllArgsConstructor
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


    @SneakyThrows
    public static void verifyPhoneNumberIsValid(String phoneNumber) {
        System.out.println(phoneNumber);
        if (phoneNumber == null) {
            throw new SpedireException("Phone number can not be null");
        }
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()){
            throw new SpedireException("Invalid phone number");
        }
    }


    @SneakyThrows
    public static void validatePhoneNumberDoesntExist(String phoneNumber, UserRepository userRepository) {
        System.out.println("a");
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            System.out.println("b");
            throw new SpedireException(String.format("User with %s exist" + phoneNumber));
        }
        System.out.println("c");
    }

    public String decodeToken(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        return decodedJWT.getClaim("phoneNumber").asString();
    }


    @SneakyThrows
    protected static void validateEmailAddress(String email) {
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            throw new SpedireException("Invalid email address");
        }
    }

    public User extractUserInformationFromToken(User user, String token) {
        DecodedJWT decodedJWT = jwtUtil.verifyToken(token);
        String firstName = decodedJWT.getClaim("given_name").asString();
        String lastName = decodedJWT.getClaim("family_name").asString();
        String email = decodedJWT.getClaim("email").asString();
        String profilePicture = decodedJWT.getClaim("picture").asString();
        user.getRoles().add(Role.NEW_USER);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setProfileImage(profilePicture);
        return user;
    }

    public VerifyPhoneNumberResponse getVerifyPhoneNumberResponse(User filledUser, String message) {
        if (message.equals("OTP Sent")) {
            User storedUser = userRepository.save(filledUser);
            String token = fetchToken(storedUser.getPhoneNumber());
            return VerifyPhoneNumberResponse.builder().message(message).token(token).build();
        } else {
            return VerifyPhoneNumberResponse.builder().message(message).build();
        }
    }

    public User buildRegistrationRequest(CompleteRegistrationRequest registrationRequest, User foundUser)  {
        foundUser.getRoles().add(SENDER);
        foundUser.setFirstName(registrationRequest.getFirstName());
        foundUser.setLastName(registrationRequest.getLastName());
        foundUser.setEmail(registrationRequest.getEmail());
        foundUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        foundUser.setProfileImage(registrationRequest.getImage());
        foundUser.setCreatedAt(LocalDateTime.now());
        return foundUser;
    }

    public String fetchToken(String phoneNumber) {
        return JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
                .withClaim("phoneNumber", phoneNumber)
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    @SneakyThrows
    public String sendEmail(String recipientEmail) {
       boolean status = javaMailService.sendMail(recipientEmail, "Welcome to Spedire", getEmailTemplate());
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
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isPresent() && !user.get().isOtpVerificationStatus()) {
            throw new SpedireException("User yet to verify Otp");
        }
    }


}

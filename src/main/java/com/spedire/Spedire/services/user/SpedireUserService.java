package com.spedire.Spedire.services.user;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.CompleteRegistrationResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.sms.SMSService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;
import static com.spedire.Spedire.services.user.UserServiceUtils.*;

@AllArgsConstructor
@Service
@Slf4j
public class SpedireUserService implements UserService{


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SMSService smsService;

    private UserServiceUtils utils;

    @Override
    @Transactional
    public VerifyPhoneNumberResponse savePhoneNumber(String phoneNumber) {
        System.out.println(phoneNumber);
        verifyPhoneNumberIsValid(phoneNumber);
        System.out.println(1);
        validatePhoneNumberDoesntExist(phoneNumber, userRepository);
        System.out.println(2);
        User user = new User(phoneNumber);
        System.out.println(3);
        user.getRoles().add(Role.NEW_USER);
        System.out.println(4);
        String message = smsService.sendVerificationSMS(phoneNumber);
        System.out.println(5);
        user.setOtpVerificationStatus(false);
        if (message.equals("OTP Sent")) {
            User storedUser = userRepository.save(user);
            String token = utils.fetchToken(storedUser.getPhoneNumber());
            return VerifyPhoneNumberResponse.builder().message(message).token(token).build();
        } else {
            return VerifyPhoneNumberResponse.builder().message(message).build();
        }
    }



    @SneakyThrows
    @Override
    @Transactional
    public CompleteRegistrationResponse completeRegistration(CompleteRegistrationRequest request, String token) {
        String phoneNumber = utils.decodeToken(token);
        utils.checkIfUserHasVerifiedOtp(phoneNumber);
        Optional<User> foundUser = userRepository.findByPhoneNumber(phoneNumber);
        if (foundUser.isEmpty()) {
            throw new SpedireException("User not found for phone number: " + phoneNumber);
        }

        validateEmailAddress(request.getEmail());

        User savedUser = userRepository.save(utils.buildRegistrationRequest(request, foundUser.get()));
        return CompleteRegistrationResponse.builder().message("USER_REGISTRATION_SUCCESSFUL").success(true).build();
//        String response = utils.sendEmail(savedUser.getEmail());
//
//        if ("Mail delivered successfully".equals(response)) {
//            return CompleteRegistrationResponse.builder().message("USER_REGISTRATION_SUCCESSFUL").success(true).build();
//        } else {
//            return CompleteRegistrationResponse.builder().message("Failed to deliver email").success(false).build();
//        }
    }

}

package com.spedire.Spedire.services.user;

import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.services.otp.OTPService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spedire.Spedire.services.user.UserServiceUtils.verifyPhoneNumberIsValid;

@AllArgsConstructor
@Service
@Slf4j
public class SpedireMemberService implements UserService{

    private final UserRepository userRepository;
    private final OTPService otpService;

    @Override
    @Transactional
    public VerifyPhoneNumberResponse verifyUserPhoneNumberFirst(String phoneNumber) {
        verifyPhoneNumberIsValid(phoneNumber);
        User user = new User(phoneNumber);
        user.getRoles().add(Role.NEW_USER);
        userRepository.save(user);
        String message = otpService.sendOTPToPhoneNumber(phoneNumber);
        return VerifyPhoneNumberResponse.builder().message("OTP sent, Check your phone").build();
    }

    @Override
    public User findMemberByMail(String email) {
        return null;
    }
}

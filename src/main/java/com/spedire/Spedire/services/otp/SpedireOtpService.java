package com.spedire.Spedire.services.otp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.OtpResponse;
import com.spedire.Spedire.models.Otp;
import com.spedire.Spedire.repositories.OtpRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.user.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;
import static com.spedire.Spedire.services.email.MailTemplates.getWelcomeMailTemplate;


@Service
public class SpedireOtpService implements OtpService{


    public SpedireOtpService(OtpRepository otpRepository, JwtUtil jwtUtil) {
        this.otpRepository = otpRepository;
        this.jwtUtil = jwtUtil;
    }

    private final OtpRepository otpRepository;

    private final JwtUtil jwtUtil;

    @Value(JWT_SECRET)
    private String secret;


    @Scheduled(cron = "0 */5 * * * ?")
    public void handleOtpTimeoutScheduled() {
        handleOtpTimeout();
    }

    private void handleOtpTimeout() {
        var allOtp = findAllOtp();
        for (Otp otp:allOtp) {
            long minutesElapsed = Duration.between(otp.getTime(), LocalTime.now()).toMinutes();
            if (minutesElapsed > 5) {
                otpRepository.delete(otp);
            }
        }
    }

    @Override
    public OtpResponse generateOtp(String phoneNumber) {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        Otp code = new Otp();
        code.setCode(otp.toString());
        code.setPhoneNumber(phoneNumber);
        code.setTime(LocalTime.now());
        otpRepository.save(code);
        return OtpResponse.builder().otpNumber(otp.toString()).build();
    }

    @Override
    public boolean verifyOtp(String otp, String token, UserService userService) throws MessagingException {
        String email = decodeToken(token);
        var otpList = findAllOtp();
        for (Otp code: otpList) {
            if (Objects.equals(code.getCode(), otp)) {
                userService.saveUser(token);
                getWelcomeMailTemplate(email);
                return true;
            }

        }
        return false;
    }

    private List<Otp> findAllOtp() {
        return otpRepository.findAll();
    }

    private String fetchToken(String phoneNumber) {
        return JWT.create().withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plusSeconds(86000L))
                .withClaim("phoneNumber", phoneNumber)
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String decodeToken(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
//        return decodedJWT.getClaim("phoneNumber").asString();
        return decodedJWT.getClaim("email").asString();
    }

}

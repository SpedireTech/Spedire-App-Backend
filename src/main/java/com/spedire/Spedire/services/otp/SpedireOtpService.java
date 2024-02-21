package com.spedire.Spedire.services.otp;

import com.spedire.Spedire.dtos.responses.OtpResponse;
import com.spedire.Spedire.exceptions.OtpException;
import com.spedire.Spedire.exceptions.PhoneNumberNotVerifiedException;
import com.spedire.Spedire.models.Otp;
import com.spedire.Spedire.repositories.OtpRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.spedire.Spedire.services.otp.OtpUtils.*;


@Service
@AllArgsConstructor
public class SpedireOtpService implements OtpService {

    private final OtpRepository otpRepository;

    private int generateOtpNumber() {
        Random random = new Random();
        return 100 + random.nextInt(500);
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void handleOtpTimeoutScheduled() {
        handleOtpTimeout();
    }

    static String generateRandomOTP(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }

        return otp.toString();
    }

    private void handleOtpTimeout() {
       List<Otp> allOtp = findAllOtp();
        for (Otp each:allOtp) {
            if((Duration.between(LocalDateTime.now(),each.getCreatedAt()).toMinutes()% 60) > 5){
                deleteOtp(each.getPhoneNumber());
            }
        }
    }

    @Override
    public OtpResponse createNewOtp(String phoneNumber){
        String otp = generateRandomOTP(6);
    var new_otp=otpRepository.save(Otp.builder().phoneNumber(phoneNumber).otpNumber(otp).createdAt(LocalDateTime.now()).build());
    if(new_otp.getId()==null){
    return OtpResponse.builder().message(OTP_CREATION_UNSUCCESSFUL).success(false).build();}
        return OtpResponse.builder().message(OTP_CREATED_SUCCESSFULLY+ phoneNumber).otpNumber(new_otp.getOtpNumber()).success(true).build();
    }

    @Override
    public void deleteOtp(String phoneNumber) {
        otpRepository.deleteByPhoneNumber(phoneNumber);
        OtpResponse.builder()
                .message(OTP_DELETED_SUCCESSFULLY)
                .build();

    }
    public OtpResponse findByPhoneNumber(String phoneNumber) throws PhoneNumberNotVerifiedException {
       Otp response =otpRepository.findByPhoneNumber(phoneNumber);
       if(response == null) throw new PhoneNumberNotVerifiedException(PHONE_NUMBER_NOT_VERIFIED);
       return OtpResponse.builder().otpNumber(response.getOtpNumber()).build();
    }

    @Override
    public Otp findByOtp(String otpNumber) throws OtpException {
        Otp response =otpRepository.findByOtpNumber(otpNumber);
        if (response != null) {
            return response;
        }
        throw new OtpException(OTP_SEARCH_FAILED);
    }

    private List<Otp>  findAllOtp(){
      return otpRepository.findAll();
    }
}

package com.spedire.Spedire.services.user;

import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.dtos.responses.CompleteRegistrationResponse;
import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;

public interface UserService {

    VerifyPhoneNumberResponse savePhoneNumber(String phoneNumber, String token);

    CompleteRegistrationResponse completeRegistration(CompleteRegistrationRequest request, String token);

}

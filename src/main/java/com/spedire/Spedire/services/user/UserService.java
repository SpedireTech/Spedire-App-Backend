package com.spedire.Spedire.services.user;

import com.spedire.Spedire.dtos.responses.VerifyPhoneNumberResponse;
import com.spedire.Spedire.repositories.models.User;

public interface UserService {

    VerifyPhoneNumberResponse verifyUserPhoneNumberFirst(String phoneNumber);

    User findMemberByMail(String email);
}

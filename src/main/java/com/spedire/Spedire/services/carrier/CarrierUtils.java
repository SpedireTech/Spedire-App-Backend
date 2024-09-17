package com.spedire.Spedire.services.carrier;

import com.spedire.Spedire.dtos.requests.UpgradeRequest;
import com.spedire.Spedire.dtos.responses.ServiceChargeResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.repositories.UserRepository;

public class CarrierUtils {

    public static void validateNotNull(UpgradeRequest request) {
        if (request.getAccountName() == null || request.getBankName() == null || request.getAccountNumber() == null
                || request.getNin() == null || request.getBvn() == null || request.getPicture() == null || request.getIdVerification() == null) {
            throw new SpedireException("All fields required");
        }
    }


    public static void validateUserExist(String email, UserRepository userRepository) {
        email = checkNoExtraCharacterBeforeAndAfterEmail(email);

        if (!userRepository.existsByEmail(email)) {
            throw new SpedireException(String.format("User with \"%s\" not found", email));
        }
    }

    static String checkNoExtraCharacterBeforeAndAfterEmail(String email) {
        email = email.replace("\"", "").replace("\\", "").trim();
        return email;
    }

    static ServiceChargeResponse mapResponse(String amount, String orderId, String authorizationUrl, String reference) {
        return new ServiceChargeResponse(amount, orderId, authorizationUrl, reference);
    }

}

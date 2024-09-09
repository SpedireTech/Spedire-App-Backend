package com.spedire.Spedire.services.carrier;

import com.spedire.Spedire.dtos.requests.ServiceChargeRequest;
import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.requests.UpgradeRequest;
import com.spedire.Spedire.dtos.responses.*;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.Bank;
import com.spedire.Spedire.models.IdVerification;
import com.spedire.Spedire.models.KYC;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.services.payment.Payment;
import com.spedire.Spedire.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spedire.Spedire.services.carrier.CarrierUtils.*;

@Service
@Slf4j
public class SpedireCarrierService implements CarrierService {


    private final UserService userService;

    private final UserRepository userRepository;

    private final Payment paymentService;

    public SpedireCarrierService(UserService userService, UserRepository userRepository, Payment paymentService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    @Override
    public UpgradeResponse upgradeToCarrier(UpgradeRequest request) {
        String carrierEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        validateUserExist(carrierEmail, userRepository);
        validateNotNull(request);
        Bank bank = Bank.builder().bankName(request.getBankName()).accountName(request.getAccountName()).accountNumber(request.getAccountNumber()).build();
        IdVerification idVerification = IdVerification.builder().idCard(request.getIdVerification()).bvn(request.getBvn()).nin(request.getNin()).build();
        KYC kyc = KYC.builder().bank(bank).idVerification(idVerification).picture(request.getPicture()).build();
        var foundUser = userService.findByEmail(checkNoExtraCharacterBeforeAndAfterEmail(carrierEmail));
       if (foundUser.isPresent() && foundUser.get().getKyc() == null) {
           User user = foundUser.get();
           user.getRoles().add(Role.CARRIER);
           user.setKyc(kyc);
           user.setUpgraded(true);
           userService.save(user);
       } else {
           return UpgradeResponse.builder().message("Upgrade already completed").build();
       }
        return UpgradeResponse.builder().message("Upgrade Successful").build();
    }


    @Override
    @Transactional
    public DowngradeCarrierResponse downgradeCarrierToSender() {
        String carrierEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        validateUserExist(carrierEmail, userRepository);
        var foundUser = userService.findByEmail(checkNoExtraCharacterBeforeAndAfterEmail(carrierEmail));
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (user.getRoles().contains(Role.CARRIER)) {
                user.getRoles().remove(Role.CARRIER);
                user.setKyc(null);
                userRepository.save(user);
                return DowngradeCarrierResponse.builder().message("Downgrade Successful").build();
            } else {
                return DowngradeCarrierResponse.builder().message("User is not a carrier").build();
            }
        } else {
            throw new SpedireException("User not found");
        }
    }


    @Override
    @Transactional
    public String addRoleSenderToUser(String email) {
        validateUserExist(email, userRepository);

        var foundUser = userService.findByEmail(email);
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (!user.getRoles().contains(Role.SENDER)) {
                user.getRoles().add(Role.SENDER);
                userRepository.save(user);
                return "Role SENDER added successfully";
            } else {
                return "User already has the SENDER role";
            }
        } else {
            throw new SpedireException("User not found");
        }
    }

    @Override
    public ServiceChargeResponse acceptServiceCharge(ServiceChargeRequest request) {
        ResponseEntity<?> response = paymentService.initiatePayment(new PaymentRequest(Integer.valueOf(request.getAmount()), "66d9e1585a083568b9346907"));
        PaymentInitializationResponse paymentResponse = (PaymentInitializationResponse) response.getBody();
        String authorizationUrl = paymentResponse.getAuthorizationUrl();
        String reference = paymentResponse.getReference();
        return mapResponse(request.getAmount(), request.getOrderId(), authorizationUrl, reference);
    }

    private ServiceChargeResponse mapResponse(String amount, String orderId, String authorizationUrl, String reference) {
        return new ServiceChargeResponse(amount, orderId, authorizationUrl, reference);
    }


    @Override
    public CheckCarrierUpgradeResponse checkCarrierUpgradeStatus() {
       CheckCarrierUpgradeResponse response = null;
        String carrierEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        validateUserExist(carrierEmail, userRepository);
        var user = userService.findByEmail(checkNoExtraCharacterBeforeAndAfterEmail(carrierEmail));
        if (user.isPresent() && user.get().getKyc() != null) {
            response = CheckCarrierUpgradeResponse.builder().status(true).message("Upgrade Completed").build();
        } else {
            response = CheckCarrierUpgradeResponse.builder().status(false).message("Yet to upgrade to carrier").build();
        }
        System.out.println(response.toString());
        return response;
    }
}

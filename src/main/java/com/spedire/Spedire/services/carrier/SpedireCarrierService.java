package com.spedire.Spedire.services.carrier;

import com.spedire.Spedire.dtos.requests.UpgradeRequest;
import com.spedire.Spedire.dtos.responses.CheckCarrierUpgradeResponse;
import com.spedire.Spedire.dtos.responses.UpgradeResponse;
import com.spedire.Spedire.dtos.responses.UserDashboardResponse;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.models.Bank;
import com.spedire.Spedire.models.IdVerification;
import com.spedire.Spedire.models.KYC;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.spedire.Spedire.services.carrier.CarrierUtils.*;

@Service
@Slf4j
public class SpedireCarrierService implements CarrierService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

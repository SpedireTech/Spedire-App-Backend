package com.spedire.Spedire.service.carrier;

import com.spedire.Spedire.dtos.requests.UpgradeRequest;
import com.spedire.Spedire.services.carrier.CarrierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CarrierTest {

    @Autowired
    private CarrierService carrierService;

    @Test
    public void upgradeUserToCarrier() {
        UpgradeRequest request = UpgradeRequest.builder().nin("88887657799").accountNumber("1462416987")
                .accountName("Olawale Cooker").bankName("GTB").picture("My pics").bvn("5643")
                .idVerification("Voters Card").build();
        var res = carrierService.upgradeToCarrier(request);
        System.out.println("Reslt -- " + res.toString());
    }

    @Test
    public void addRoleSenderToUserroles() {
        var res = carrierService.addRoleSenderToUser("spediretech@gmail.com");
        System.out.println("Reslt -- " + res);
    }


    @Test
    public void checkCarrierUpgradeStatus() {
        var res = carrierService.checkCarrierUpgradeStatus();
        System.out.println("Reslt -- " + res.toString());
    }
}

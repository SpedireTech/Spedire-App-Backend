package com.spedire.Spedire.services.carrier;

import com.spedire.Spedire.dtos.requests.ServiceChargeRequest;
import com.spedire.Spedire.dtos.requests.UpgradeRequest;
import com.spedire.Spedire.dtos.responses.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface CarrierService {

    UpgradeResponse upgradeToCarrier(UpgradeRequest request);
    DowngradeCarrierResponse downgradeCarrierToSender();

    CheckCarrierUpgradeResponse checkCarrierUpgradeStatus();
    String addRoleSenderToUser(String email);

    ServiceChargeResponse acceptServiceCharge(ServiceChargeRequest request);
    List<Object> matchOrderRequest(String senderLocation, String senderTown, String orderId) throws Exception;


}

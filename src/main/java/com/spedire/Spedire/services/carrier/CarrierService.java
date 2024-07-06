package com.spedire.Spedire.services.carrier;

import com.spedire.Spedire.dtos.requests.UpgradeRequest;
import com.spedire.Spedire.dtos.responses.CheckCarrierUpgradeResponse;
import com.spedire.Spedire.dtos.responses.UpgradeResponse;
import com.spedire.Spedire.dtos.responses.UserDashboardResponse;

public interface CarrierService {

    UpgradeResponse upgradeToCarrier(UpgradeRequest request);

    CheckCarrierUpgradeResponse checkCarrierUpgradeStatus();


}

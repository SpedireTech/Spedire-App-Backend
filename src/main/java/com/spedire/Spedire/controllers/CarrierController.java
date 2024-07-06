package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.UpgradeRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.CheckCarrierUpgradeResponse;
import com.spedire.Spedire.dtos.responses.UpgradeResponse;
import com.spedire.Spedire.dtos.responses.UserDashboardResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.carrier.CarrierService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.spedire.Spedire.controllers.Utils.*;

@AllArgsConstructor
@RequestMapping("/api/v1/carrier")
@RestController
@Slf4j
public class CarrierController {

    private final CarrierService carrierService;

    @PostMapping("/upgrade")
    public ResponseEntity<ApiResponse<?>> upgradeToCarrier(@RequestBody UpgradeRequest request) {
        try {
            UpgradeResponse response = carrierService.upgradeToCarrier(request);
            if (response.getMessage().equals(UPGRADE_SUCCESSFUL)) {
                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(true).build());
            } else if (response.getMessage().equals("Upgrade already completed")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder().message(response.getMessage()).success(false).build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message("Bad Request! Upgrade failed").data(response).success(false).build());
            }
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder().message("An unexpected error occurred").success(false).build());
        }
    }


    @PostMapping("/status")
    public ResponseEntity<ApiResponse<?>> checkKYCStatus() {
        CheckCarrierUpgradeResponse response;
        try {
            response = carrierService.checkCarrierUpgradeStatus();
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message(response.getMessage()).success(response.isStatus()).build());
        } catch (SpedireException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message(exception.getMessage()).success(false).build());
        }
    }


}

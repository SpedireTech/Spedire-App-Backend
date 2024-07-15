package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.SavedAddressResponse;
import com.spedire.Spedire.services.savedAddress.Address;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.spedire.Spedire.controllers.Utils.INCOMPLETE_REGISTRATION;

@AllArgsConstructor
@RequestMapping("/api/v1/address")
@RestController
@Slf4j
public class AddressController {

    private final Address savedAddress;

    @GetMapping("/sender")
    public ResponseEntity<ApiResponse<?>> getSenderSavedAddress() {
        Set<String> response = savedAddress.getSenderSavedAddress();
        if (!response.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(ApiResponse.builder().message("Saved Address").data(response).success(true).build());
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(ApiResponse.builder().message("Empty").success(false).build());
        }
    }

    @GetMapping("/receiver")
    public ResponseEntity<ApiResponse<?>> getReceiverSavedAddress() {
        Set<String> response = savedAddress.getReceiverSavedAddress();
        if (!response.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(ApiResponse.builder().message("Saved Address").data(response).success(true).build());
        } else {
            return ResponseEntity.status(org.springframework.http.HttpStatus.OK).body(ApiResponse.builder().message("Empty").data(response).success(false).build());
        }
    }

}

package com.spedire.Spedire.services.savedAddress;

import com.spedire.Spedire.dtos.responses.SavedAddressResponse;

import java.util.List;
import java.util.Set;

public interface Address {

    void saveAddress(String senderAddress, String receiverAddress);

    Set<String> getSenderSavedAddress();

    Set<String> getReceiverSavedAddress();
}

package com.spedire.Spedire.services.savedAddress;

import com.spedire.Spedire.dtos.responses.SavedAddressResponse;
import com.spedire.Spedire.repositories.SavedAddressRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import com.spedire.Spedire.models.SavedAddress;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
@Slf4j
public class SavedAddressImpl implements Address{

    private final SavedAddressRepository savedAddressRepository;


    @Override
    public void saveAddress(String senderAddress, String receiverAddress) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println("Email is = " + email);
        SavedAddress savedAddress = SavedAddress.builder().senderAddress(senderAddress).receiverAddress(receiverAddress).email(email).build();
        savedAddressRepository.save(savedAddress);
    }

    @Override
    public Set<String> getSenderSavedAddress() {
        Set<String> responses = new HashSet<>();
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<SavedAddress> savedAddresses = savedAddressRepository.findByEmail(email);

        for (SavedAddress address: savedAddresses) {
            if (address.getSenderAddress() != null && !address.getSenderAddress().isEmpty()) {
                responses.add(address.getSenderAddress());
            }
        }
        return responses;
    }


    @Override
    public Set<String> getReceiverSavedAddress() {
        Set<String> response = new HashSet<>();
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<SavedAddress> savedAddresses = savedAddressRepository.findByEmail(email);

        for (SavedAddress address : savedAddresses) {
            if (address.getReceiverAddress() != null && !address.getReceiverAddress().isEmpty()) {
                response.add(address.getReceiverAddress());
            }
        }
        return response;
    }
}

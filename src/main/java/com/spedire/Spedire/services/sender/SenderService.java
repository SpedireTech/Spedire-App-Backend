package com.spedire.Spedire.services.sender;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.models.SenderPool;
import com.spedire.Spedire.services.carrier.CarrierService;
import jakarta.mail.MessagingException;

import java.util.List;

public interface SenderService {

    List<Object> findMatch(String orderId, CarrierService carrierService) throws Exception;

    void saveSenderRequestInAPool(CreateOrderRequest createOrderRequest, String fullName, String id);

    Object selectCarrier(SelectCarrierRequest request) throws MessagingException;

    List<SenderPool> findOrderBySenderTown(String carrierTown);
}

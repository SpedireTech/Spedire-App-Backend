package com.spedire.Spedire.services.sender;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.models.SenderPool;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.services.carrier.CarrierService;
import jakarta.mail.MessagingException;

import java.util.List;

public interface SenderService {



    void saveSenderRequestInAPool(CreateOrderRequest createOrderRequest, User user, String orderId);

    List<SenderPool> findOrderBySenderTown(String carrierTown);
}

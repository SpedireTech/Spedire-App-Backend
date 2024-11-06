package com.spedire.Spedire.services.sender;

import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.SenderPool;

import java.util.List;
import java.util.Optional;

public interface SenderService {



    void saveSenderRequestInAPool(Order order);

    List<SenderPool>  findOrderBySenderTown(String carrierTown);

    Optional<SenderPool> findById(String orderId);
}

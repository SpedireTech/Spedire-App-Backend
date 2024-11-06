package com.spedire.Spedire.repositories;

import com.spedire.Spedire.enums.OrderStatus;
import com.spedire.Spedire.models.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends MongoRepository<Delivery, String> {

    List<Delivery> findDeliveryByCarrierTownAndOrderStatus(String carrierTown, OrderStatus orderStatus);


    List<Delivery> findDeliveryByCarrierTown(String senderTown);

}

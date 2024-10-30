package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeliveryRepository extends MongoRepository<Delivery, String> {

}

package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.MatchedDelivery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MatchedDeliveryRepository extends MongoRepository<MatchedDelivery, String> {

    Optional<MatchedDelivery> findByDeliveryId(String orderId);

    boolean existsByDeliveryId(String orderId);

}

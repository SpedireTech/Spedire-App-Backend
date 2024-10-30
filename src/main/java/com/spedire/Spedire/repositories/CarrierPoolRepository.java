package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.CarrierPool;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CarrierPoolRepository extends MongoRepository<CarrierPool, String> {

    List<CarrierPool> findCarrierPoolByCarrierTown(String town);

    Optional<CarrierPool> findByOrderId(String orderId);

}

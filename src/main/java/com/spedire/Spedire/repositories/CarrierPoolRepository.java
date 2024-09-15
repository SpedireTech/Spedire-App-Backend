package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.CarrierPool;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CarrierPoolRepository extends MongoRepository<CarrierPool, String> {

    List<CarrierPool> findCarrierPoolByCarrierTown(String town);

}

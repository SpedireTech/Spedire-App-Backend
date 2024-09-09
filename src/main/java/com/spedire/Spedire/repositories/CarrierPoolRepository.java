package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.CarrierPool;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarrierPoolRepository extends MongoRepository<CarrierPool, String> {
}

package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.CarrierPool;
import com.spedire.Spedire.models.SenderPool;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SenderPoolRepository extends MongoRepository<SenderPool, String>  {

    List<SenderPool> findOrderBySenderTown(String carrierTown);

}

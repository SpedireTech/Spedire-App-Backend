package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.SenderPool;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SenderPoolRepository extends MongoRepository<SenderPool, String>  {

    List<SenderPool> findSenderPoolByOrder_SenderTown(String town);
    Optional<SenderPool> findSenderPoolByOrder_Id(String orderId);


}

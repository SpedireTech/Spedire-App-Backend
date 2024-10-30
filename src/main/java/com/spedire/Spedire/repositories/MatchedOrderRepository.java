package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.MatchedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MatchedOrderRepository extends MongoRepository<MatchedOrder, String> {


    boolean existsByOrderId(String orderId);

    Optional<MatchedOrder> findByOrderId(String orderId);


}

package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.MatchedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchedOrderRepository extends MongoRepository<MatchedOrder, String> {


}

package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Order;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchedOrderRepository extends MongoRepository<Order, String> {
}

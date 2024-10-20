package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompletedOrderRepository extends MongoRepository<Order, String> {
}

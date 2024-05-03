package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

}

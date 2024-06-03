package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.services.order.AcceptedORder.AcceptedOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AcceptedOrderRepository extends MongoRepository<Order, String> {
}

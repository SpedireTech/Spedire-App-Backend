package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.CarrierPool;
import com.spedire.Spedire.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findOrderBySenderTown(String town);



}

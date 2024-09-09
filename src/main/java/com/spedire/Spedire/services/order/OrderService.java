package com.spedire.Spedire.services.order;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.models.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    CreateOrderResponse<?> createOrder(CreateOrderRequest createOrderRequest);


    Optional<Order> findOrderById(String orderId);

    void saveOrder(Order foundOrder);

    Optional<Order> findOrderByReference(String reference);

    List<Order> findOrdersByPaymentStatus(String pending);

    void deleteOrder(Order order);

}

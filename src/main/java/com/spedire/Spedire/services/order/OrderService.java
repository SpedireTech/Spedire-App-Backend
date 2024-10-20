package com.spedire.Spedire.services.order;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.services.carrier.CarrierService;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    CreateOrderResponse<?> createOrder(CreateOrderRequest createOrderRequest, CarrierService carrierService) throws Exception;

    Optional<Order> findOrderById(String orderId);

    void saveOrder(Order foundOrder);

    Optional<Order> findOrderByReference(String reference);

    List<Order> findOrdersByPaymentStatus(String pending);

    void deleteOrder(Order order);

    List<Order> pendingOrderHistory(String token);

    List<Order> completedOrderHistory(String token);

    List<Order> acceptedOrderHistory(String token);
}

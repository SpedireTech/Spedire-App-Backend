package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.services.carrier.CarrierService;
import com.spedire.Spedire.services.order.AcceptedORder.AcceptedOrder;
import com.spedire.Spedire.services.order.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.spedire.Spedire.controllers.Utils.INCOMPLETE_REGISTRATION;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    private final AcceptedOrder acceptedOrder;

    private final CarrierService carrierService;

    @PostMapping("/createOrder")
    public CreateOrderResponse<?>  createOrder(@RequestBody CreateOrderRequest order) throws Exception {
        return orderService.createOrder(order, carrierService);
    }

    @PostMapping("/matchOrder")
    public ResponseEntity<?> matchOrder(@RequestBody MatchedOrderDto order) {
        var response = acceptedOrder.matchOrder(order);
        return ResponseEntity.status(HttpStatus.SC_OK).body(response);
    }

    @GetMapping("/acceptOrder")
    public ResponseEntity<?> acceptOrder(@RequestBody AcceptedOrderDto order) {
        var response = acceptedOrder.acceptOrder(order);
        return ResponseEntity.status(HttpStatus.SC_OK).body(ApiResponse.builder().message("Order Accepted").data(response).build());
    }

    @GetMapping("/acceptedOrders")

    public ResponseEntity<?> acceptedOrders() {
        var response = acceptedOrder.senderAcceptedOrders();
        return ResponseEntity.status(HttpStatus.SC_OK).body(ApiResponse.builder().message("Here are list of your accepted orders").data(response).build());
    }
    @GetMapping("/pendingOrderHistory")

    public ResponseEntity<?> pendingOrderHistory(String token) {
        var response = orderService.pendingOrderHistory(token);
        return ResponseEntity.status(HttpStatus.SC_OK).body(ApiResponse.builder().message("Pending Order History").data(response).build());
    }


    @GetMapping("/acceptedOrderHistory")

    public ResponseEntity<?> acceptedOrderHistory(String token) {
        var response = orderService.acceptedOrderHistory(token);
        return ResponseEntity.status(HttpStatus.SC_OK).body(ApiResponse.builder().message("Accepted Order History").data(response).build());
    }

    @GetMapping("/completedOrderHistory")

    public ResponseEntity<?> completedOrderHistory(String token) {
        var response = orderService.completedOrderHistory(token);
        return ResponseEntity.status(HttpStatus.SC_OK).body(ApiResponse.builder().message("Accepted Order History").data(response).build());
    }




}

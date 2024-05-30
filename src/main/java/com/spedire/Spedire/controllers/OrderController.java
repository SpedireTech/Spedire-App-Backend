package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.services.order.AcceptedORder.AcceptedOrder;
import com.spedire.Spedire.services.order.OrderService;
import lombok.AllArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    private final AcceptedOrder acceptedOrder;


    @PostMapping("/createOrder")

    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest order) {
     var response = orderService.createOrder(order);
     return ResponseEntity.status(HttpStatus.SC_OK).body(response);
    }

    @GetMapping("/matchOrder")

    public ResponseEntity<?> matchOrder(@RequestBody MatchedOrderDto order) {
        var response = acceptedOrder.matchOrder(order);
        return ResponseEntity.status(HttpStatus.SC_OK).body(response);
    }

}

package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.carrier.CarrierService;
import com.spedire.Spedire.services.order.AcceptedORder.AcceptedOrder;
import com.spedire.Spedire.services.order.OrderService;
import com.spedire.Spedire.services.sender.SenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
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
@Tag(name = "Orders", description = "Endpoints related to Orders")
public class OrderController {
    private final OrderService orderService;

    private final AcceptedOrder acceptedOrder;

    private final CarrierService carrierService;

    private final SenderService senderService;

    @PostMapping("/createOrder")
    @Operation(summary = "Create a new order", description = "This endpoint is used to create a new Delivery order.")
    public CreateOrderResponse<?>  createOrder(@RequestBody CreateOrderRequest order) throws Exception {
        return orderService.createOrder(order, carrierService, senderService);
    }

    @PostMapping("/matchOrder")
    public ResponseEntity<?> matchOrder(@RequestBody MatchedOrderDto order) throws MessagingException {
        var response = acceptedOrder.matchOrder(order);
        return ResponseEntity.status(HttpStatus.SC_OK).body(response);
    }


    @GetMapping("/find-match")
    public ResponseEntity<?> findMatch(@RequestParam String orderId) {
        try {
            var matchResults = orderService.findMatch(orderId, carrierService);
            return ResponseEntity.status(HttpStatus.SC_OK).body(matchResults);
        } catch (SpedireException e) {
            log.error("SpedireException: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
                    .body(ApiResponse.builder().message("Order not found: " + e.getMessage()).success(false).build());
        } catch (Exception e) {
            log.error("General error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder().message("An error occurred: " + e.getMessage()).success(false).build());
        }
    }


    @PostMapping("/select-carrier")
    public ResponseEntity<ApiResponse<?>> selectCarrier(@RequestBody SelectCarrierRequest request) {
        try {
            Object response = orderService.selectCarrier(request);

            return ResponseEntity.ok(
                    ApiResponse.builder().message("Carrier selection successful").success(true).data(response).build());
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder().message("Failed to send notification email: " + e.getMessage()).success(false).build());
        } catch (SpedireException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder().message(e.getMessage()).success(false).build());
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.builder().message("An unexpected error occurred: " + e.getMessage()).success(false).build());
        }
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

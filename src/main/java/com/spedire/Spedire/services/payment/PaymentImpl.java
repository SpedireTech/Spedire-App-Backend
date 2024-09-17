package com.spedire.Spedire.services.payment;

import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.PaymentInitializationResponse;
import com.spedire.Spedire.dtos.responses.PaymentVerificationResponse;
import com.spedire.Spedire.enums.PaymentStatus;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.OrderPayment;
import com.spedire.Spedire.services.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PaymentImpl implements Payment {

    private static final Logger logger = LoggerFactory.getLogger(PaymentImpl.class);
    private final PaymentUtils paymentUtils;
    private final OrderService orderService;

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    public PaymentImpl(PaymentUtils paymentUtils, OrderService orderService) {
        this.paymentUtils = paymentUtils;
        this.orderService = orderService;
    }


    @Override
    public ResponseEntity<?> initiatePayment(PaymentRequest paymentRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        logger.info("Initiating payment for email: {}", email);
        paymentUtils.validateUserInTheContext(email.replace("\"", "").trim());
        Optional<Order> order = paymentUtils.validateAndFindOrderId(paymentRequest.getOrderId());
        Order foundOrder;
        if (order.isPresent()) {
            foundOrder = order.get();
            if (foundOrder.isPayState()) {
                logger.warn("Order {} has already been paid for.", paymentRequest.getOrderId());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder().message("The order has already been paid for").success(false).build());
            }
        } else {
            logger.error("Order {} not found.", paymentRequest.getOrderId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder().message("Order not found").success(false).build());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        headers.set("Content-Type", "application/json");

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("email", email.replace("\"", "").trim());
        requestPayload.put("amount", paymentRequest.getAmount() * 100);
        requestPayload.put("channels", new String[]{"card", "bank", "ussd", "qr", "bank_transfer"});

        HttpEntity<String> request = new HttpEntity<>(requestPayload.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange("https://api.paystack.co/transaction/initialize", HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String authorizationUrl = jsonResponse.getJSONObject("data").getString("authorization_url");
            String reference = jsonResponse.getJSONObject("data").getString("reference");
            logger.info("Authorization URL: {}", authorizationUrl);
            logger.info("Reference: {}", reference);
            foundOrder.setOrderPayment(OrderPayment.builder().transactionReference(reference).paymentStatus(PaymentStatus.PENDING).initiatedAt(LocalDateTime.now()).build());
            orderService.saveOrder(foundOrder);
            PaymentInitializationResponse initializationResponse = PaymentInitializationResponse.builder().authorizationUrl(authorizationUrl).reference(reference).build();
            return ResponseEntity.ok(initializationResponse);
        } else {
            logger.error("Failed to initiate payment. Status code: {}, Response body: {}", response.getStatusCode(), response.getBody());
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }


    @Override
    public ResponseEntity<?> verifyPayment(String reference) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        paymentUtils.validateUserInTheContext(email.replace("\"", "").trim());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://api.paystack.co/transaction/verify/" + reference, HttpMethod.GET, entity, String.class);
        validatePaymentVerificationResponse(reference, email, response);
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONObject dataObject = jsonResponse.getJSONObject("data");
        PaymentVerificationResponse verificationResponse = PaymentVerificationResponse.builder().reference(dataObject.getString("reference")).status(dataObject.getString("status")).currency(dataObject.getString("currency")).channel(dataObject.getString("channel")).paid_at(dataObject.getString("paid_at")).amount(String.valueOf(dataObject.getLong("amount"))).build();
        return ResponseEntity.ok(verificationResponse);
    }

    @Override
    public PaymentVerificationResponse updatePaymentInfoFromWebhook(String reference, JSONObject jsonPayload) {
        Optional<Order> orderOptional = orderService.findOrderByReference(reference);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            JSONObject data = jsonPayload.getJSONObject("data");
            String status = data.getString("status");
            PaymentStatus paymentStatus = getPaymentStatus(status, order);

            order.setOrderPayment(OrderPayment.builder()
                    .transactionReference(reference)
                    .paymentStatus(paymentStatus)
                    .userEmail(data.getJSONObject("customer").getString("email"))
                    .amount(BigDecimal.valueOf(data.getLong("amount")))
                    .currency(data.getString("currency"))
                    .paymentChannel(data.getString("channel"))
                    .initiatedAt(LocalDateTime.parse(data.getString("created_at").replace("Z", "")))
                    .completedAt(LocalDateTime.parse(data.getString("paid_at").replace("Z", "")))
                    .gatewayResponse(data.getString("gateway_response"))
                    .status(status)
                    .build());
            orderService.saveOrder(order);

            return PaymentVerificationResponse.builder()
                    .reference(reference)
                    .status(status)
                    .currency(data.getString("currency"))
                    .channel(data.getString("channel"))
                    .paid_at(data.getString("paid_at"))
                    .amount(String.valueOf(data.getLong("amount")))
                    .build();
        } else {
            logger.error("Order with reference {} not found.", reference);
            return null;
        }
    }
    private void validatePaymentVerificationResponse(String reference, String email, ResponseEntity<String> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONObject data = jsonResponse.getJSONObject("data");

            String status = data.getString("status");
            Optional<Order> orderOptional = orderService.findOrderByReference(reference);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                PaymentStatus paymentStatus = getPaymentStatus(status, order);

                order.setOrderPayment(OrderPayment.builder().transactionReference(reference).paymentStatus(paymentStatus).userEmail(email).amount(BigDecimal.valueOf(data.getLong("amount"))).currency(data.getString("currency")).paymentChannel(data.getString("channel")).initiatedAt(LocalDateTime.parse(data.getString("created_at").replace("Z", ""))).completedAt(LocalDateTime.parse(data.getString("paid_at").replace("Z", ""))).gatewayResponse(data.getString("gateway_response")).status(status).build());
                orderService.saveOrder(order);
            } else {
                logger.error("Order with reference {} not found.", reference);
            }
        } else {
            logger.error("Failed to verify payment. Status code: {}, Response body: {}", response.getStatusCode(), response.getBody());
        }
    }

    private static PaymentStatus getPaymentStatus(String status, Order order) {
        PaymentStatus paymentStatus;
        switch (status) {
            case "success" -> {
                paymentStatus = PaymentStatus.PAID;
                order.setPayState(true);
            }
            case "failed" -> paymentStatus = PaymentStatus.FAILED;
            case "processing" -> paymentStatus = PaymentStatus.PROCESSING;
            case "abandoned" -> paymentStatus = PaymentStatus.ABANDONED;
            case "reversed" -> paymentStatus = PaymentStatus.REVERSED;
            default -> paymentStatus = PaymentStatus.UNKNOWN;
        }
        return paymentStatus;
    }

}



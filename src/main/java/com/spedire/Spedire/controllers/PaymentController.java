package com.spedire.Spedire.controllers;


import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.requests.RegistrationRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.PaymentVerificationResponse;
import com.spedire.Spedire.dtos.responses.RegistrationResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.payment.Payment;
import com.spedire.Spedire.services.payment.PaymentStatusHandler;
import com.spedire.Spedire.services.websocket.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.xml.bind.DatatypeConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.util.Objects;

import static com.spedire.Spedire.controllers.Utils.extractMessageFromResponseBody;
import static com.spedire.Spedire.controllers.Utils.verifySignature;


@Validated
@RestController
@Slf4j
@RequestMapping("/api/v1/payment")
public class PaymentController {

    static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final Payment payment;

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    private final WebSocketService webSocketService;


    public PaymentController(WebSocketService webSocketService, Payment payment) {
        this.webSocketService = webSocketService;
        this.payment = payment;
    }


    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@Valid @RequestBody PaymentRequest request) {
        ResponseEntity<?> response = payment.initiatePayment(request);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .message("Payment Initialized")
                    .data(response.getBody())
                    .success(true)
                    .build());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
    }


    @GetMapping("/verify/{reference}")
    public ResponseEntity<ApiResponse<?>> verifyPayment(@PathVariable String reference) {
        ResponseEntity<?> response = payment.verifyPayment(reference);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(new ApiResponse<>("Payment verification successful", true, response.getBody()));
        } else {
            String responseBody = Objects.requireNonNull(response.getBody()).toString();
            String errorMessage = extractMessageFromResponseBody(responseBody);
            return ResponseEntity.status(response.getStatusCode())
                    .body(new ApiResponse<>(errorMessage, false, null));
        }
    }



    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(
            @RequestHeader("x-paystack-signature") String signature,
            @RequestBody String payload) {

        logger.info("Initializing PayStack Webhook");
        if (!verifySignature(payload, signature, paystackSecretKey)) {
            logger.error("Invalid signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        try {
            JSONObject jsonPayload = new JSONObject(payload);
            String event = jsonPayload.getString("event");

            if (event.equals("charge.success")) {
                JSONObject data = jsonPayload.getJSONObject("data");
                String reference = data.getString("reference");
                logger.info("Webhook successfully received for reference: {}", reference);

                PaymentVerificationResponse response = payment.updatePaymentInfoFromWebhook(reference, jsonPayload);
                var responseEntity = ResponseEntity.ok(ApiResponse.builder().message("Payment record updated").success(true).data(response).build());
                webSocketService.sendMessage("/topic/payment-status", responseEntity.getBody());

                return responseEntity;
            } else {
                logger.warn("Unhandled event: {}", event);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unhandled event");
            }
        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }




}

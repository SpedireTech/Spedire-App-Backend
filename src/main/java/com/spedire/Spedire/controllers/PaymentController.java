package com.spedire.Spedire.controllers;


import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.requests.RegistrationRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.RegistrationResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.payment.Payment;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


@RequestMapping("/api/v1/")
@RestController
@Slf4j
public class PaymentController {

    private final Payment payment;

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    public PaymentController(Payment payment) {
        this.payment = payment;
    }


    @PostMapping("pay")
    public ResponseEntity<ApiResponse<?>> initiatePayment(@RequestBody PaymentRequest request) {
        ResponseEntity<?> response = payment.initiatePayment(request);
        System.out.println("Response.getBody == " + response);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .message("Payment Initialized")
                    .data(response.getBody())
                    .success(true)
                    .build());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(ApiResponse.builder()
                    .message("Payment Initialization Failed")
                    .data(response.getBody())
                    .success(false)
                    .build());
        }
    }


    @GetMapping("/verify/{reference}")
    public ResponseEntity<ApiResponse<String>> verifyPayment(@PathVariable String reference) {
        ResponseEntity<String> response = payment.verifyPayment(reference);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(new ApiResponse<>("Payment verification successful", true, response.getBody()));
        } else {
            return ResponseEntity.status(response.getStatusCode())
                    .body(new ApiResponse<>("Payment verification failed",false, response.getBody()));
        }
    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader("x-paystack-signature") String signature,
            @RequestBody String payload) {

        if (!verifySignature(payload, signature, paystackSecretKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        try {
            JSONObject jsonPayload = new JSONObject(payload);
            String event = jsonPayload.getString("event");

            if (event.equals("charge.success")) {
                JSONObject data = jsonPayload.getJSONObject("data");
                String reference = data.getString("reference");

                System.out.println("Payment successful for reference: " + reference);

                return ResponseEntity.ok("Webhook received");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unhandled event");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    private boolean verifySignature(String payload, String signature, String secretKey) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] signedBytes = sha256Hmac.doFinal(payload.getBytes());
            String expectedSignature = new String(Hex.encodeHex(signedBytes));
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }


}

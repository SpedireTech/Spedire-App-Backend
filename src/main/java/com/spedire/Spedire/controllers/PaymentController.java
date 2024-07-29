package com.spedire.Spedire.controllers;


import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.requests.RegistrationRequest;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.PaymentVerificationResponse;
import com.spedire.Spedire.dtos.responses.RegistrationResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.payment.Payment;
import com.spedire.Spedire.services.payment.PaymentStatusHandler;
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


@Validated
@RestController
@Slf4j
@RequestMapping("/api/v1/")
public class PaymentController {

    static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final Payment payment;

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;


    private final PaymentStatusHandler paymentStatusHandler;


    public PaymentController(PaymentStatusHandler paymentStatusHandler, Payment payment) {
        this.paymentStatusHandler = paymentStatusHandler;
        this.payment = payment;
    }


    @PostMapping("payment")
    public ResponseEntity<ApiResponse<?>> initiatePayment(@Valid @RequestBody PaymentRequest request) {
        ResponseEntity<?> response = payment.initiatePayment(request);
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


    @GetMapping("verify-payment/{reference}")
    public ResponseEntity<ApiResponse<?>> verifyPayment(@PathVariable String reference) {
        ResponseEntity<?> response = payment.verifyPayment(reference);
        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(new ApiResponse<>("Payment verification successful", true, response.getBody()));
        } else {
            return ResponseEntity.status(response.getStatusCode())
                    .body(new ApiResponse<>("Payment verification failed",false, response.getBody()));
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
//                System.out.println("response.getStats == " + response.getStatus());
                try {
                    paymentStatusHandler.sendPaymentStatusUpdate(response.getStatus());
                } catch (Exception exception) {
                    logger.error("Error sending WebSocket message", exception);
                }

                return ResponseEntity.ok(ApiResponse.builder().message("Payment record updated").success(true).data(response).build());
            } else {
                logger.warn("Unhandled event: {}", event);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unhandled event");
            }
        } catch (Exception e) {
            logger.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }



    private boolean verifySignature(String payload, String signature, String secretKey) {
        try {
            String HMAC_SHA512 = "HmacSHA512";
            byte[] byteKey = secretKey.getBytes("UTF-8");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            Mac sha512_HMAC = Mac.getInstance(HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            byte[] macData = sha512_HMAC.doFinal(payload.getBytes("UTF-8"));
            String expectedSignature = DatatypeConverter.printHexBinary(macData).toLowerCase();
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            logger.error("Error verifying signature", e);
            return false;
        }
    }


}

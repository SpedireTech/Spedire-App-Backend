package com.spedire.Spedire.services.payment;

import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.responses.PaymentResponse;
import com.spedire.Spedire.dtos.responses.PaymentVerificationResponse;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public interface Payment {

    ResponseEntity<?> initiatePayment(PaymentRequest request);

    ResponseEntity<?> verifyPayment(String reference);

    PaymentVerificationResponse updatePaymentInfoFromWebhook(String reference, JSONObject jsonPayload);
}

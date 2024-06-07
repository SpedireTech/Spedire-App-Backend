package com.spedire.Spedire.services.payment;

import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.responses.PaymentResponse;
import org.springframework.http.ResponseEntity;

public interface Payment {

    ResponseEntity<?> initiatePayment(PaymentRequest request);
    ResponseEntity<String> verifyPayment(String reference);
}

package com.spedire.Spedire.services.payment;

import com.spedire.Spedire.dtos.requests.PaymentRequest;
import com.spedire.Spedire.dtos.responses.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class PaymentImpl implements Payment {


    @Value("${paystack.secret.key}")
    private String paystackSecretKey;


    @Override
    public ResponseEntity<?> initiatePayment(PaymentRequest paymentRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        headers.set("Content-Type", "application/json");

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("email", paymentRequest.getEmail());
        requestPayload.put("amount", paymentRequest.getAmount());
        requestPayload.put("channels", new String[]{"card", "bank", "ussd", "qr", "bank_transfer"});

        HttpEntity<String> request = new HttpEntity<>(requestPayload.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.paystack.co/transaction/initialize",
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String authorizationUrl = jsonResponse.getJSONObject("data").getString("authorization_url");
            return ResponseEntity.ok(authorizationUrl);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }

    }



    @Override
    public ResponseEntity<String> verifyPayment(String reference) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + paystackSecretKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.paystack.co/transaction/verify/" + reference,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response;
    }


//    @Override
//    public ResponseEntity<?> initiatePayment(PaymentRequest paymentRequest) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + paystackSecretKey);
//        headers.set("Content-Type", "application/json");
//
//        HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://api.paystack.co/transaction/initialize",
//                HttpMethod.POST,
//                request,
//                String.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            JSONObject jsonResponse = new JSONObject(response.getBody());
//            String authorizationUrl = jsonResponse.getJSONObject("data").getString("authorization_url");
//            return ResponseEntity.ok(authorizationUrl);
//        } else {
//            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
//        }
//    }


}



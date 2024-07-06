package com.spedire.Spedire.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentRequest {

    private String email;
    private int amount;
//    private int orderId;

}

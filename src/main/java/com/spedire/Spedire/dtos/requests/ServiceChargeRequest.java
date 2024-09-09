package com.spedire.Spedire.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceChargeRequest {

    private String amount;
    private String orderId;
}

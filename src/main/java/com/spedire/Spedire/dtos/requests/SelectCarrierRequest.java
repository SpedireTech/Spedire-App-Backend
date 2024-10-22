package com.spedire.Spedire.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectCarrierRequest {

    private String email;
    private String orderId;
}

package com.spedire.Spedire.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class CreateOrderResponse {

    private String message;

    private boolean status;

}

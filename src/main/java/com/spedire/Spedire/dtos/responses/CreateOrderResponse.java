package com.spedire.Spedire.dtos.responses;

import lombok.*;

@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class CreateOrderResponse {

    private String message;

    private boolean status;

}

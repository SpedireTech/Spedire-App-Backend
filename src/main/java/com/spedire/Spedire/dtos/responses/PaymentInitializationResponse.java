package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Setter
@ToString
public class PaymentInitializationResponse {

    private String authorizationUrl;
    private String reference;
}

package com.spedire.Spedire.dtos.responses;

import lombok.*;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ServiceChargeResponse {

    private String amount;
    private String orderId;
    private String authorizationUrl;
    private String reference;


}

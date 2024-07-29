package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Setter
@ToString
public class PaymentVerificationResponse {

    private String status;

    private  String reference;
    private String amount;

    private String currency;
    private String paid_at;

    private String channel;


}

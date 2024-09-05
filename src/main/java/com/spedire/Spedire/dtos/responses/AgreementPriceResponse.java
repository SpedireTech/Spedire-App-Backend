package com.spedire.Spedire.dtos.responses;

import lombok.*;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class AgreementPriceResponse {

    private String amount;
    private String authorizationUrl;
    private String reference;

}

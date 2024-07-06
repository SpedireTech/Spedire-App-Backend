package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Setter
@ToString
public class CheckCarrierUpgradeResponse {

    private boolean status;
    private String message;

}

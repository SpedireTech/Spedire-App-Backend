package com.spedire.Spedire.dtos.requests;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MatchedOrderDto {

    private String currentLocation;
    private String destination;
    private String carrierTown;

}

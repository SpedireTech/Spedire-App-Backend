package com.spedire.Spedire.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MatchedOrderDto {

    private String currentLocation;
    private String destination;
    private String carrierTown;

}

package com.spedire.Spedire.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class MatchedOrderResponse {

    private String message;
    private boolean status;
    private List<CarrierListDtoResponse> matchedOrders;


}

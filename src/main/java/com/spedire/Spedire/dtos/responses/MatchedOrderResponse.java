package com.spedire.Spedire.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class MatchedOrderResponse {

    private String message;
    private boolean status;
    private Map<String, Object> data;
//    private List<CarrierListDtoResponse> matchedOrders;


}

package com.spedire.Spedire.dtos.responses;

import com.spedire.Spedire.models.Order;
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

    private List<OrderListDtoResponse> matchedOrders;
}

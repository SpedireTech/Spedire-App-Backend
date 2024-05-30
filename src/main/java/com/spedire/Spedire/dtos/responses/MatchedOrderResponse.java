package com.spedire.Spedire.dtos.responses;

import com.spedire.Spedire.models.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder

public class MatchedOrderResponse {

    private List<OrderListDtoResponse> matchedOrders;
}

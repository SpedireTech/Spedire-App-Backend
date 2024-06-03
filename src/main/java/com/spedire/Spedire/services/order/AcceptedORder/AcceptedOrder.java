package com.spedire.Spedire.services.order.AcceptedORder;

import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.dtos.responses.MatchedOrderResponse;

import java.util.List;

public interface AcceptedOrder {

    MatchedOrderResponse matchOrder (MatchedOrderDto matchedOrderDto);


    AcceptedOrderResponse acceptOrder (AcceptedOrderDto acceptedOrderDto);

   List<AcceptedOrderResponseForSender> senderAcceptedOrders();

}

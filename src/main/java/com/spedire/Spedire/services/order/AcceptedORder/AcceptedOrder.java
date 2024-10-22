package com.spedire.Spedire.services.order.AcceptedORder;

import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.dtos.responses.MatchedOrderResponse;
import jakarta.mail.MessagingException;

import java.util.List;

public interface AcceptedOrder {

    MatchedOrderResponse matchOrder (MatchedOrderDto matchedOrderDto) throws MessagingException;

    AcceptedOrderResponse acceptOrder (AcceptedOrderDto acceptedOrderDto);

   List<AcceptedOrderResponseForSender> senderAcceptedOrders();

}

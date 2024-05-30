package com.spedire.Spedire.services.order.AcceptedORder;

import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.MatchedOrderResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.services.order.OrderService;
import com.spedire.Spedire.services.order.OrderUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SpedireAcceptedOrder implements AcceptedOrder{

    private final OrderRepository orderRepository;



    @Override
    public MatchedOrderResponse matchOrder(MatchedOrderDto matchedOrderDto) {

       var allOrders = orderRepository.findAll();

       List<Order> matchedOrders = new ArrayList<>();

       for (Order order : allOrders) {
          var senderPossibleLocations =  order.getSenderLocation();
          var carrierPossibleLocations = matchedOrderDto.getCurrentLocation();
          boolean commonLocation =  senderPossibleLocations.stream().anyMatch(carrierPossibleLocations::contains);
          if (!commonLocation) break;

       String receiverPossibleLocations =  order.getReceiverLocation();
       String carrierCurrentLocation = matchedOrderDto.getDestination();

       if (receiverPossibleLocations.equals(carrierCurrentLocation)){
           matchedOrders.add(order);
       }

       }
        var response =  matchedOrders.stream().map(OrderUtils::convertFromOrderToOrderListDto).toList();
       return MatchedOrderResponse.builder().matchedOrders(response).build();

    }

    @Override
    public AcceptedOrderResponse acceptOrder(AcceptedOrderDto acceptedOrderDto) {
        return null;
    }
}

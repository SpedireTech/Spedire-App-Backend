package com.spedire.Spedire.services.order.AcceptedORder;

import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.dtos.responses.MatchedOrderResponse;
import com.spedire.Spedire.models.CarrierPool;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.*;
import com.spedire.Spedire.services.order.OrderUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SpedireAcceptedOrder implements AcceptedOrder{

    private final OrderRepository orderRepository;

   private final CarrierPoolRepository carrierPoolRepository;
    private final AcceptedOrderRepository acceptedOrderRepository;

    private final UserRepository userRepository;



    @Override
    public MatchedOrderResponse matchOrder(MatchedOrderDto matchedOrderDto) {

       var allOrders = orderRepository.findAll();

       List<Order> matchedOrders = new ArrayList<>();
//
       for (Order order : allOrders) {
           var senderLocation = order.getSenderTown();
           if (matchedOrderDto.getCurrentLocation().equals(senderLocation) && matchedOrderDto.getDestination().equals(order.getReceiverTown())) matchedOrders.add(order);
       }

//          var senderPossibleLocations =  order.getSenderLocation();
//          var carrierPossibleLocations = matchedOrderDto.getCurrentLocation();
//          boolean commonLocation =  senderPossibleLocations.stream().anyMatch(carrierPossibleLocations::contains);
//          if (!commonLocation) break;
//
//       String receiverPossibleLocations =  order.getReceiverLocation();
//       String carrierCurrentLocation = matchedOrderDto.getDestination();
//
//       if (receiverPossibleLocations.equals(carrierCurrentLocation)){
//           matchedOrders.add(order);
//       }
//
//       }
        CarrierPool carrierPool = new CarrierPool();
       carrierPool.setName("Dummy Name");
       carrierPool.setDestination(matchedOrderDto.getDestination());
       carrierPool.setCurrentLocation(matchedOrderDto.getCurrentLocation());
       carrierPoolRepository.save(carrierPool);

        var response =  matchedOrders.stream().map(OrderUtils::convertFromOrderToOrderListDto).toList();
       return MatchedOrderResponse.builder().matchedOrders(response).build();

    }

    @Override
    public AcceptedOrderResponse acceptOrder(AcceptedOrderDto acceptedOrderDto) {
        String carrierId = "";
        Order acceptedOrder = new Order();


        for (Order order : orderRepository.findAll()) {
            if (order.getId().equals(acceptedOrderDto.getOrderId())){
                order.setCarriedId(carrierId);
                order.setItemValue(BigDecimal.valueOf(1000));
                acceptedOrder = order;
            }
        }
        Optional<User> foundUser = userRepository.findById(carrierId);
        acceptedOrder.setCarrierImage(foundUser.get().getProfileImage());
        acceptedOrder.setCarrierFullName(foundUser.get().getFullName());
        acceptedOrder.setCarrierPhoneNumber(foundUser.get().getPhoneNumber());
        acceptedOrderRepository.save(acceptedOrder);
      return AcceptedOrderUtils.convertOrderToOrderResponseForCarrier(acceptedOrder);


    }

    @Override
    public List<AcceptedOrderResponseForSender> senderAcceptedOrders() {
        String senderId = "";
        var orders = acceptedOrderRepository.findAll();
        var allAcceptedOrders = new ArrayList<Order>();

        for (Order order : orders) {
            if (order.getSenderId().equals(senderId)){
                allAcceptedOrders.add(order);
            }
        }

        var acceptedOrders = new ArrayList<AcceptedOrderResponseForSender>();
        for (Order order : allAcceptedOrders) {
            var response = AcceptedOrderUtils.convertOrderToOrderResponseForSender(order);
            acceptedOrders.add(response);
        }
        return acceptedOrders;

    }

}

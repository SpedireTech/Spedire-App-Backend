package com.spedire.Spedire.services.order.AcceptedORder;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.dtos.responses.MatchedOrderResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.AcceptedOrderRepository;
import com.spedire.Spedire.repositories.CarrierDeliveryRepository;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.models.CarrierPool;
import com.spedire.Spedire.repositories.*;
import com.spedire.Spedire.services.order.OrderUtils;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.spedire.Spedire.services.user.UserServiceUtils.EMAIL;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@Service
@AllArgsConstructor
public class SpedireAcceptedOrder implements AcceptedOrder{

    private final OrderRepository orderRepository;
    private AcceptedOrderUtils utils;
    private final CarrierPoolRepository carrierPoolRepository;
    private final AcceptedOrderRepository acceptedOrderRepository;

    private final HttpServletRequest request;
    private final UserRepository userRepository;
    private final UserService userService;
    private final OrderUtils orderUtils;



    @Override
    public MatchedOrderResponse matchOrder(MatchedOrderDto matchedOrderDto) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        var allOrders = orderRepository.findOrderBySenderTown(matchedOrderDto.getCarrierTown());
        List<Order> matchedOrders = new ArrayList<>();
        for (Order order : allOrders) {
            var senderLocation = order.getSenderTown();
            if (senderLocation.equals(matchedOrderDto.getCarrierTown())) matchedOrders.add(order);
        }
        if (!matchedOrders.isEmpty()) {
            var response = matchedOrders.stream().map(order -> {
                try {
                    return orderUtils.convertFromOrderToOrderListDto(order, matchedOrderDto.getCurrentLocation());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            return MatchedOrderResponse.builder().status(true).message("We found some order going your way").matchedOrders(response).build();
        } else {
            CarrierPool carrierPool = new CarrierPool();
            carrierPool.setName(user.getFullName());
            carrierPool.setPhoneNumber(user.getPhoneNumber());
            carrierPool.setDeliveryCount(user.getDeliveryCount());
            carrierPool.setRating("4");
            carrierPool.setDestination(matchedOrderDto.getDestination());
            carrierPool.setCurrentLocation(matchedOrderDto.getCurrentLocation());
            carrierPool.setCarrierTown(matchedOrderDto.getCarrierTown());
            carrierPoolRepository.save(carrierPool);
            return MatchedOrderResponse.builder().status(true).message("Please hold! We are matching your request").build();
        }

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

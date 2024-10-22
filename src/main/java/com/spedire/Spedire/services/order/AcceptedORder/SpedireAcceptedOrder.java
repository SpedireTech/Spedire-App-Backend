package com.spedire.Spedire.services.order.AcceptedORder;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.dtos.responses.MatchedOrderResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.SenderPool;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.AcceptedOrderRepository;
import com.spedire.Spedire.repositories.CarrierDeliveryRepository;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.models.CarrierPool;
import com.spedire.Spedire.repositories.*;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.order.OrderUtils;
import com.spedire.Spedire.services.review.ReviewInterface;
import com.spedire.Spedire.services.sender.SenderService;
import com.spedire.Spedire.services.user.UserService;
import jakarta.mail.MessagingException;
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
    private final JavaMailService javaMailService;
    private final HttpServletRequest request;
    private final UserRepository userRepository;
    private final ReviewInterface reviewInterface;
    private final UserService userService;
    private final SenderPoolRepository senderPoolRepository;
    private final SenderService senderService;
    private final OrderUtils orderUtils;


    @Override
    public MatchedOrderResponse matchOrder(MatchedOrderDto matchedOrderDto) throws MessagingException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));


        List<SenderPool> allOrders = senderService.findOrderBySenderTown(matchedOrderDto.getCarrierTown());
        System.out.println("allOrders::: "+ allOrders);
        List<SenderPool> matchedOrders = new ArrayList<>();
        if (allOrders.size() != 0) {
            for (SenderPool order : allOrders) {
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
            javaMailService.sendMail(email, "Match Found", "");
            return MatchedOrderResponse.builder().status(true).message("We found some order going your way").matchedOrders(response).build();
        }
        CarrierPool carrierPool = new CarrierPool();
        carrierPool.setName(user.getFullName());
        carrierPool.setPhoneNumber(user.getPhoneNumber());
        carrierPool.setDeliveryCount(user.getDeliveryCount());
        carrierPool.setRating(user.getReviewId() != null ? String.valueOf(reviewInterface.getRating(user.getReviewId())) : "No Rating");
        carrierPool.setEmail(user.getEmail());
        carrierPool.setDestination(matchedOrderDto.getDestination());
        carrierPool.setCurrentLocation(matchedOrderDto.getCurrentLocation());
        carrierPool.setCarrierTown(matchedOrderDto.getCarrierTown());
        carrierPoolRepository.save(carrierPool);
        javaMailService.sendMail(email, "No Match Found", "");
        return MatchedOrderResponse.builder().status(true).message("Please hold! We are matching your request").build();
    }
        return null;
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

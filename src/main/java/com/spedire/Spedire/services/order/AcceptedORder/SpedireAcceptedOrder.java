package com.spedire.Spedire.services.order.AcceptedORder;

import com.spedire.Spedire.dtos.requests.AcceptedOrderDto;
import com.spedire.Spedire.dtos.requests.MatchedOrderDto;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.dtos.responses.CarrierListDtoResponse;
import com.spedire.Spedire.dtos.responses.MatchedOrderResponse;
import com.spedire.Spedire.enums.OrderStatus;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.*;
import com.spedire.Spedire.repositories.AcceptedOrderRepository;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.repositories.UserRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.spedire.Spedire.services.email.MailTemplates.matchFoundTemplate;
import static com.spedire.Spedire.services.email.MailTemplates.noMatchFoundTemplate;

@Service
@AllArgsConstructor
public class SpedireAcceptedOrder implements AcceptedOrder{

    private final OrderRepository orderRepository;
    private AcceptedOrderUtils utils;
    private final CarrierPoolRepository carrierPoolRepository;
    private final DeliveryRepository deliveryRepository;
    private final MatchedDeliveryRepository matchedDeliveryRepository;
    private final AcceptedOrderRepository acceptedOrderRepository;
    private final JavaMailService javaMailService;
    private final HttpServletRequest request;
    private final UserRepository userRepository;
    private final ReviewInterface reviewInterface;
    private final UserService userService;
    private final SenderService senderService;
    private final OrderUtils orderUtils;



    @Override
    public MatchedOrderResponse matchOrder(MatchedOrderDto matchedOrderDto) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().replace("\"", "");
        User user = userService.findByEmail(email).orElseThrow(() -> new SpedireException("User not found with email: " + email));
        List<SenderPool> allOrders = senderService.findOrderBySenderTown(matchedOrderDto.getCarrierTown());
        List<SenderPool> matchedOrders = allOrders.stream()
                .filter(order -> order.getSenderTown().equals(matchedOrderDto.getCarrierTown())).collect(Collectors.toList());
        Delivery delivery = Delivery.builder().carrierTown(matchedOrderDto.getCarrierTown()).currentLocation(matchedOrderDto.getCurrentLocation())
                .createdAt(LocalDateTime.now()).userId(user.getId()).destination(matchedOrderDto.getDestination()).build();
        Delivery savedDelivery = deliveryRepository.save(delivery);
        Map<String, Object> deliveryInfo = new LinkedHashMap<>();
        deliveryInfo.put("referenceId", savedDelivery.getId());
        deliveryInfo.put("carrierTown", savedDelivery.getCarrierTown());
        deliveryInfo.put("carrierDestination", savedDelivery.getDestination());

        if (!matchedOrders.isEmpty()) {
            List<CarrierListDtoResponse> response = matchedOrders.stream()
                    .map(order -> {
                        try {
                            return orderUtils.convertFromOrderToOrderListDto(order, matchedOrderDto.getCurrentLocation());
                        } catch (Exception exception) {
                            throw new RuntimeException("Failed to convert order to DTO", exception);
                        }
                    }).toList();
            System.out.println("Response -- " + response);
            MatchedDelivery matchedDelivery = MatchedDelivery.builder().deliveryId(savedDelivery.getId()).matchedOrders(response).build();
            matchedDeliveryRepository.save(matchedDelivery);
            String mailContent = matchFoundTemplate(matchedOrders.size(), "https://spedire.netlify.app/login", savedDelivery.getId());
            javaMailService.sendMail(email, "Match Found", mailContent);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("deliveryInfo", deliveryInfo);
            data.put("matchedOrders", response);
            return MatchedOrderResponse.builder().status(true).message("We found some orders going your way").data(data).build();
        }

        CarrierPool carrierPool = CarrierPool.builder().orderId(savedDelivery.getId()).name(user.getFullName()).phoneNumber(user.getPhoneNumber())
                .deliveryCount(String.valueOf(user.getDeliveryCount())).rating(user.getReviewId() != null ? String.valueOf(reviewInterface.getRating(user.getReviewId())) : "No Rating")
                .email(user.getEmail()).destination(matchedOrderDto.getDestination()).currentLocation(matchedOrderDto.getCurrentLocation())
                .carrierTown(matchedOrderDto.getCarrierTown()).build();
        carrierPoolRepository.save(carrierPool);
        javaMailService.sendMail(email, "Matching in Progress", noMatchFoundTemplate(savedDelivery.getId()));
        return MatchedOrderResponse.builder().status(true).message("Please hold! We are matching your request").data(deliveryInfo).build();
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

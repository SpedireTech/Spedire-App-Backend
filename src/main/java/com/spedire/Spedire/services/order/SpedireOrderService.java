package com.spedire.Spedire.services.order;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.enums.OrderType;
import com.spedire.Spedire.exceptions.*;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.OrderPayment;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.repositories.*;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.carrier.CarrierService;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.savedAddress.Address;
import com.spedire.Spedire.services.sender.SenderService;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.spedire.Spedire.services.user.UserServiceUtils.EMAIL;
import static com.spedire.Spedire.services.user.UserServiceUtils.INVALID_EMAIL_ADDRESS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@Service
@Slf4j
public class SpedireOrderService implements OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AcceptedOrderRepository acceptedOrderRepository;
    private final CompletedOrderRepository completedOrderRepository;
    private final Address savedAddress;
    private final HttpServletRequest request;
    private final JavaMailService javaMailService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OrderUtils utils;
    private static final String PHONE_NUMBER_REGEX = "^(080|091|070|081|090)\\d{8}$";
    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);

    @Override
    @Transactional
    public CreateOrderResponse<?>  createOrder(CreateOrderRequest createOrderRequest, CarrierService carrierService, SenderService senderService) throws Exception {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        DecodedJWT decodedJWT = utils.extractTokenDetails(authorizationHeader);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        validateRequest(createOrderRequest);
        Order order = buildOrder(createOrderRequest, user);
        Order savedOrder = orderRepository.save(order);
        javaMailService.sendMail(user.getEmail(), "Send a Package", "Hey there! Your order request is received and awaiting match");
        log.info("New Order received with id: {}", savedOrder.getId());
        saveAddress(createOrderRequest);
        List<Object> matchResult = carrierService.matchOrderRequest(createOrderRequest.getSenderLocation(), createOrderRequest.getSenderTown(), savedOrder.getId());
        Map<String, Object> orderInfo = new LinkedHashMap<>();
        orderInfo.put("referenceId", savedOrder.getId()); orderInfo.put("orderName", savedOrder.getItemName());
        if (matchResult.size() != 0) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("orderInfo", orderInfo);
            map.put("couriers", matchResult);
            senderService.saveSenderRequestInAPool(createOrderRequest, user.getFullName(), user.getId());
            return CreateOrderResponse.builder().status(true).message("We found you some pretty nice match").data(map).build();
        }
        senderService.saveSenderRequestInAPool(createOrderRequest, user.getFullName(), user.getId());
        return CreateOrderResponse.builder().status(true).message("Order has been successfully created").data(orderInfo).build();
    }



    @Override
    public Optional<Order> findOrderById(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Order not found");
        }
        return order;
    }

    @Override
    public void saveOrder(Order foundOrder) {
        orderRepository.save(foundOrder);
    }


    @Override
    public Optional<Order> findOrderByReference(String reference) {
        List<Order> allOrders = orderRepository.findAll();
        for (Order order : allOrders) {
            OrderPayment orderPayment = order.getOrderPayment();
            if (orderPayment != null && reference.equals(orderPayment.getTransactionReference())) {
                return Optional.of(order);
            }
        }
        return Optional.empty();
    }



    @Override
    public List<Order> findOrdersByPaymentStatus(String pending) {
        List<Order> orders = new ArrayList<>();
        List<Order> allOrder = orderRepository.findAll();
        for (Order order : allOrder) {
            OrderPayment orderPayment = order.getOrderPayment();
            if (orderPayment != null && orderPayment.getPaymentStatus().name().equals(pending)) {
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }

    @Override
    public List<Order> pendingOrderHistory(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException(INVALID_EMAIL_ADDRESS));
        String userId = user.getId();

        return orderRepository.findAll().stream().filter(order -> order.getCarriedId().equals(userId) || order.getSenderId().equals(userId)).toList();
    }

    @Override
    public List<Order> completedOrderHistory(String token) {

        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException(INVALID_EMAIL_ADDRESS));
        String userId = user.getId();
        return completedOrderRepository.findAll().stream().filter(order -> order.getCarriedId().equals(userId) || order.getSenderId().equals(userId)).toList();

    }

    @Override
    public List<Order> acceptedOrderHistory(String token) {
        String splitToken = token.split(" ")[1];
        DecodedJWT decodedJWT = jwtUtil.verifyToken(splitToken);
        String email = decodedJWT.getClaim(EMAIL).asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SpedireException(INVALID_EMAIL_ADDRESS));
        String userId = user.getId();

        return acceptedOrderRepository.findAll().stream().filter(order -> order.getCarriedId().equals(userId) || order.getSenderId().equals(userId)).toList();

    }

    private void saveAddress(CreateOrderRequest createOrderRequest) {
        boolean isSenderAddressSaved = createOrderRequest.isSaveSenderAddress();
        boolean isReceiverAddressSaved = createOrderRequest.isSaveReceiverAddress();

        if (isSenderAddressSaved && isReceiverAddressSaved) {
            savedAddress.saveAddress(createOrderRequest.getSenderLocation(), createOrderRequest.getReceiverLocation());
        } else if (isSenderAddressSaved) {
            savedAddress.saveAddress(createOrderRequest.getSenderLocation(), "");
        } else if (isReceiverAddressSaved) {
            savedAddress.saveAddress("", createOrderRequest.getReceiverLocation());
        }
    }



    private void validateRequest(CreateOrderRequest createOrderRequest) {
        if (createOrderRequest.getItemValue() == null) throw new NullValueException("Item value is null");
        if (createOrderRequest.getItemName() == null) throw new NullValueException("Item name is null");
        if (createOrderRequest.getDueDate() == null) throw new NullValueException("Due date is null");
        if (createOrderRequest.getDueTime() == null) throw new NullValueException("Due time is null");
        if (createOrderRequest.getSenderLocation() == null) throw new NullValueException("Sender location is required");
        if (createOrderRequest.getReceiverLocation() == null) throw new NullValueException("Receiver location is required");
        if (createOrderRequest.getReceiverName() == null) throw new NullValueException("Receiver name cannot be null");
        if (createOrderRequest.getReceiverPhoneNumber() == null) throw new NullValueException("Receiver phone number is null");
        verifyPhoneNumberIsValid(createOrderRequest.getReceiverPhoneNumber());
    }

    private Order buildOrder(CreateOrderRequest createOrderRequest, User user) {
        Order order = new Order();
        try {
            order.setDueDate(dateConverter(createOrderRequest.getDueDate()));
        } catch (ParseException exception) {
            throw new InvalidDateException("Invalid date format. Please provide the date in 'MM/dd/yyyy' format.");
        }

        try {
            order.setDueTime(timeConverter(createOrderRequest.getDueTime()));
        } catch (ParseException exception) {
            throw new InvalidTimeException("Invalid time format. Please provide time in 'hh:mm a' format.");
        }

        order.setPicture(createOrderRequest.getPicture());
        order.setItemValue(new BigDecimal(createOrderRequest.getItemValue()));
        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setReceiverLocation(createOrderRequest.getReceiverLocation());
        order.setReceiverPhoneNumber(createOrderRequest.getReceiverPhoneNumber());
        order.setSenderLocation(createOrderRequest.getSenderLocation());
        order.setSenderId(user.getId());
        order.setSenderName(user.getFullName());
        order.setItemName(createOrderRequest.getItemName());
        order.setPickUpNote(createOrderRequest.getPickUpNote());
        order.setSenderTown(createOrderRequest.getSenderTown());
        order.setDropOffNote(createOrderRequest.getDropOffNote());
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderType(OrderType.AWAITING_MATCH);
        return order;
    }


    private static LocalTime timeConverter(String timeString) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        try {
            return LocalTime.parse(timeString, formatter);
        } catch (Exception e) {
            throw new ParseException("Invalid time format", 0);
        }
    }

    private static Date dateConverter(String date) throws ParseException {
        String format = "MM/dd/yyyy";
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }

    public static void verifyPhoneNumberIsValid(String phoneNumber) {
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()){
            throw new SpedireException("Invalid phone number");
        }
    }

}

package com.spedire.Spedire.services.order;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.requests.SelectCarrierRequest;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.dtos.responses.FindMatchResponse;
import com.spedire.Spedire.enums.OrderStatus;
import com.spedire.Spedire.exceptions.*;
import com.spedire.Spedire.models.*;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.repositories.*;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.carrier.CarrierService;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.location.mapBox.MapBoxService;
import com.spedire.Spedire.services.savedAddress.Address;
import com.spedire.Spedire.services.sender.SenderService;
import com.spedire.Spedire.services.user.UserService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.spedire.Spedire.services.email.MailTemplates.*;
import static com.spedire.Spedire.services.user.UserServiceUtils.EMAIL;
import static com.spedire.Spedire.services.user.UserServiceUtils.INVALID_EMAIL_ADDRESS;

@AllArgsConstructor
@Service
@Slf4j
public class SpedireOrderService implements OrderService {


    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AcceptedOrderRepository acceptedOrderRepository;
    private final CompletedOrderRepository completedOrderRepository;
    private final DeliveryRepository deliveryRepository;
    private final CarrierPoolRepository carrierPoolRepository;
    private final Address savedAddress;
    private final SenderService senderService;
    private final MapBoxService mapBoxService;
    private final JavaMailService javaMailService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OrderUtils utils;
    private static final String PHONE_NUMBER_REGEX = "^(080|091|070|081|090)\\d{8}$";
    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);


    @Override
    @Transactional
    public CreateOrderResponse<?>  createOrder(CreateOrderRequest createOrderRequest, CarrierService carrierService, SenderService senderService) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().replace("\"", "");
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        validateRequest(createOrderRequest);
        Order savedOrder = buildOrder(createOrderRequest, user);
        log.info("New Order received with id: {}", savedOrder.getId());
        saveAddress(createOrderRequest);

        //Match the order, with sender location, town, orderId
        List<Object> matchResult = matchOrderRequestForSender(createOrderRequest.getSenderLocation(), createOrderRequest.getSenderTown());
        Map<String, Object> orderInfo = new LinkedHashMap<>();
        orderInfo.put("referenceId", savedOrder.getId()); orderInfo.put("orderName", savedOrder.getItemName());
        if (matchResult.size() != 0) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("orderInfo", orderInfo);
            map.put("couriers", matchResult);
            javaMailService.sendMail(user.getEmail(), "Match Found", matchFoundTemplate(matchResult.size(), "https://spedire.netlify.app/login", savedOrder.getId()));
            return CreateOrderResponse.builder().status(true).message("We found you some pretty nice match").data(map).build();
        }
        javaMailService.sendMail(email, "Matching in Progress", noMatchFoundTemplate(savedOrder.getId()));
        senderService.saveSenderRequestInAPool(savedOrder);
        return CreateOrderResponse.builder().status(true).message("Order has been successfully created").data(orderInfo).build();
    }


    @Override
    public FindMatchResponse<?> findMatch(String orderId, CarrierService carrierService) throws Exception {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().replace("\"", "");
        userService.findByEmail(email).orElseThrow(() -> new SpedireException("User not found"));
        //For Sender
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            List<Object> matchResult = matchOrderRequestForSender(order.get().getSenderLocation(), order.get().getSenderTown());
            Map<String, Object> orderInfo = new LinkedHashMap<>();
            orderInfo.put("referenceId", order.get().getId()); orderInfo.put("orderName", order.get().getItemName());
            if (matchResult.size() != 0) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("orderInfo", orderInfo);
                map.put("couriers", matchResult);
                return FindMatchResponse.builder().status(true).message("We found you some pretty nice match").data(map).build();
            }
            else {
                return FindMatchResponse.builder().status(false).message("No Match Yet").data(new ArrayList<>()).build();
            }
        }
        //Carrier
        else {
            Optional<CarrierPool> carriers = carrierPoolRepository.findByOrderId(orderId);
            if (carriers.isPresent()) {
                List<Object> matchResult = matchOrderRequestForCarriers(carriers.get().getCurrentLocation(), carriers.get().getCarrierTown());
                if (matchResult.size() != 0) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("couriers", matchResult);
                    return FindMatchResponse.builder().status(true).message("We found you some pretty nice match").data(map).build();
                }
                else {
                    return FindMatchResponse.builder().status(false).message("No Match Yet").data(new ArrayList<>()).build();
                }
            }

        }
        return FindMatchResponse.builder().status(false).message("Invalid Reference Id").data(new ArrayList<>()).build();
    }

    private List<Object> matchOrderRequestForCarriers(String currentLocation, String carrierTown) throws Exception {
        List<Object> objectList = new ArrayList<>();
        List<SenderPool> allOrders = senderService.findOrderBySenderTown(carrierTown);
        if (!allOrders.isEmpty()) {
            for (SenderPool sender : allOrders) {
                objectList.add(buildSenderInfoMap(currentLocation, sender, null));
            }
        } else {
            List<Order> order = orderRepository.findOrderBySenderTown(carrierTown);
            for (Order singleOrder : order) {
                objectList.add(buildSenderInfoMap(currentLocation, null, singleOrder));
            }

        }
        return objectList;
    }


    @Override
    @Transactional
    public Object selectCarrier(SelectCarrierRequest request) throws MessagingException {
        return null;
    }

    @Override
    public List<Object> matchOrderRequestForSender(String senderLocation, String senderTown) throws Exception {
        List<Object> objectList = new ArrayList<>();
        List<CarrierPool> carriersInTown = carrierPoolRepository.findCarrierPoolByCarrierTown(senderTown);
        if (!carriersInTown.isEmpty()) {
            for (CarrierPool carrier : carriersInTown) {
                objectList.add(buildCarrierInfoMap(senderLocation, carrier, null));
            }
        } else {
            List<Delivery> deliverers = deliveryRepository.findDeliveryByCarrierTown(senderTown);
            if (!deliverers.isEmpty()) {
                for (Delivery delivery : deliverers) {
                    objectList.add(buildCarrierInfoMap(senderLocation, null, delivery));
                }
            }
        }
        return objectList;
    }



    private Map<String, String> buildSenderInfoMap(String carrierLocation, SenderPool sender, Order order) throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        String minutesAway;
        User user;
        if (sender != null && !sender.getOrder().getSenderId().isEmpty()) {
            minutesAway = mapBoxService.getMinutesAway(carrierLocation, sender.getOrder().getSenderLocation());
            user = userRepository.findById(sender.getOrder().getSenderId()).get();
            map.put("name", user.getFullName());
            map.put("email", user.getEmail());
            map.put("minutesAway", minutesAway);
            map.put("town", sender.getOrder().getSenderTown() + " Lagos");
            map.put("number", user.getPhoneNumber());
            map.put("rating", user.getRating());
            map.put("deliveryCount", String.valueOf(user.getDeliveryCount()));
        }
        else if (order != null && !order.getSenderId().isEmpty()){
            user = userRepository.findById(order.getSenderId()).get();
            minutesAway = mapBoxService.getMinutesAway(carrierLocation, order.getSenderLocation());
            map.put("name", user.getFullName());
            map.put("email", user.getEmail());
            map.put("minutesAway", minutesAway);
            map.put("town", order.getSenderTown() + " Lagos");
            map.put("number", user.getPhoneNumber());
            map.put("rating", user.getRating());
            map.put("deliveryCount", String.valueOf(user.getDeliveryCount()));
        }
        return map;
    }

    private Map<String, String> buildCarrierInfoMap(String senderLocation, CarrierPool carrier, Delivery delivery) throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        String minutesAway;
        if (delivery != null && !delivery.getUserId().isEmpty()) {
            minutesAway = mapBoxService.getMinutesAway(senderLocation, delivery.getCurrentLocation());
            User user = userRepository.findById(delivery.getUserId()).get();
            map.put("name", user.getFullName());
            map.put("email", user.getEmail());
            map.put("minutesAway", minutesAway);
            map.put("town", delivery.getCarrierTown() + " Lagos");
            map.put("number", user.getPhoneNumber());
            map.put("rating", user.getRating());
            map.put("deliveryCount", String.valueOf(user.getDeliveryCount()));
        }
        else if (carrier != null) {
            minutesAway = mapBoxService.getMinutesAway(senderLocation, carrier.getCurrentLocation());
            map.put("name", carrier.getName());
            map.put("email", carrier.getEmail());
            map.put("minutesAway", minutesAway);
            map.put("town", carrier.getCarrierTown() + " Lagos");
            map.put("number", carrier.getPhoneNumber());
            map.put("rating", carrier.getRating());
            map.put("deliveryCount", carrier.getDeliveryCount());
        }
        return map;
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
        LocalDateTime dueDateTime;

        try {
            Date dueDate = dateConverter(createOrderRequest.getDueDate());
            LocalTime dueTime = timeConverter(createOrderRequest.getDueTime());
            dueDateTime = LocalDateTime.ofInstant(dueDate.toInstant(), ZoneId.systemDefault()).with(dueTime);
            order.setDueDate(dueDate);
            order.setDueTime(dueTime);
        } catch (ParseException exception) {
            throw new InvalidDateException("Invalid date format. Please provide the date in 'MM/dd/yyyy' format.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (dueDateTime.isBefore(now)) {
            throw new InvalidDateException("Due date and time must be in the future.");
        }
        if (dueDateTime.toLocalDate().isEqual(now.toLocalDate()) && dueDateTime.toLocalTime().isBefore(now.toLocalTime())) {
            throw new InvalidDateException("Due time must be in the future if the due date is today.");
        }
        order.setPicture(createOrderRequest.getPicture());
        order.setItemValue(new BigDecimal(createOrderRequest.getItemValue()));
        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setReceiverLocation(createOrderRequest.getReceiverLocation());
        order.setReceiverPhoneNumber(createOrderRequest.getReceiverPhoneNumber());
        order.setSenderLocation(createOrderRequest.getSenderLocation());
        order.setSenderId(user.getId());
        order.setSenderPhoneNumber(user.getPhoneNumber());
        order.setSenderName(user.getFullName());
        order.setItemName(createOrderRequest.getItemName());
        order.setPickUpNote(createOrderRequest.getPickUpNote());
        order.setSenderTown(createOrderRequest.getSenderTown());
        order.setDropOffNote(createOrderRequest.getDropOffNote());
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.AWAITING_MATCH);
        return orderRepository.save(order);
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

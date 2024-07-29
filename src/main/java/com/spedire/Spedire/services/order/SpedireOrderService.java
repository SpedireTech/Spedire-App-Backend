package com.spedire.Spedire.services.order;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.exceptions.*;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.models.OrderPayment;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.services.savedAddress.Address;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Service
@Slf4j
public class SpedireOrderService implements OrderService {

    private final OrderRepository orderRepository;

    private final Address savedAddress;

    private static final String PHONE_NUMBER_REGEX = "^(080|091|070|081|090)\\d{8}$";

    private static final Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        validateRequest(createOrderRequest);
        Order order = buildOrder(createOrderRequest);
        orderRepository.save(order);
        saveAddress(createOrderRequest);
        return CreateOrderResponse.builder().status(true).message("Order has been successfully created").build();
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


//    @Override
//    public List<Order> findOrdersByPaymentStatus(String pending) {
//        List<Order> orders = new ArrayList<>();
//        OrderPayment orderPayment;
//        List<Order> allOrder = orderRepository.findAll();
//        for (Order order: allOrder) {
//            orderPayment = order.getOrderPayment();
//            if (orderPayment.getPaymentStatus().name().equals(pending)) {
//                orders.add(order);
//            }
//        }
//        return orders;
//    }


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

    private Order buildOrder(CreateOrderRequest createOrderRequest) {
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
        order.setSenderId("664e339fca817508f16db8e6");
        order.setItemName(createOrderRequest.getItemName());
        order.setPickUpNote(createOrderRequest.getPickUpNote());
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

package com.spedire.Spedire.services.order;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
@AllArgsConstructor
@Service
public class SpedireOrderService implements OrderService{

    private final UserRepository userRepository;
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        Order order = new Order();
        try {
            order.setDueDate(dateConverter(createOrderRequest));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        order.setDueTime(timeConverter(createOrderRequest));
        order.setPicture(createOrderRequest.getPicture());
        order.setItemDescription(createOrderRequest.getItemDescription());
        order.setPrice(new BigDecimal(createOrderRequest.getPrice()));


        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setReceiverLocation(createOrderRequest.getReceiverLocation());
        order.setReceiverPhoneNumber(createOrderRequest.getReceiverPhoneNumber());
        order.setReceiverName(createOrderRequest.getReceiverName());


        order.setSenderLocation(createOrderRequest.getSenderLocation());
        order.setSenderId(createOrderRequest.getSenderId());
        order.setSenderPhoneNumber(createOrderRequest.getSenderPhoneNumber());


        return CreateOrderResponse.builder().status(true).message("Order has been successfully create").build();
    }


    public static LocalTime timeConverter(CreateOrderRequest createOrderRequest) {
        String timeString = createOrderRequest.getDueTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeString, formatter);
    }

    public static Date dateConverter(CreateOrderRequest createOrderRequest) throws ParseException {
        String date = createOrderRequest.getDueDate();
        String format = "mm/dd/yyyy";
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);
    }
}

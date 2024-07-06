package com.spedire.Spedire.services.order;

import com.spedire.Spedire.dtos.requests.CreateOrderRequest;
import com.spedire.Spedire.dtos.responses.CreateOrderResponse;
import com.spedire.Spedire.models.Order;
import com.spedire.Spedire.repositories.OrderRepository;
import com.spedire.Spedire.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SpedireOrderService implements OrderService{

    private final OrderRepository orderRepository;
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {

        log.info("Create Order");
        Order order = new Order();
        try {
            order.setDueDate(dateConverter(createOrderRequest));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        log.info("hello ");
        order.setSenderName(createOrderRequest.getSenderName());
        order.setDueTime(timeConverter(createOrderRequest));
        order.setPicture(createOrderRequest.getPicture());
        order.setItemDescription(createOrderRequest.getItemDescription());
        order.setPrice(new BigDecimal(createOrderRequest.getPrice()));
        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setReceiverLocation(createOrderRequest.getReceiverLocation());
        order.setReceiverPhoneNumber(createOrderRequest.getReceiverPhoneNumber());
        order.setReceiverName(createOrderRequest.getReceiverName());
        order.setSenderLocation(createOrderRequest.getSenderLocation());
        order.setSenderId("664e339fca817508f16db8e6");
        order.setSenderPhoneNumber(createOrderRequest.getSenderPhoneNumber());
        order.setItemName(createOrderRequest.getItemName());
        order.setItemValue(createOrderRequest.getItemValue());
        order.setPickUpNote(createOrderRequest.getPickUpNote());

        log.info("type");
        orderRepository.save(order);
        log.info("reach here");
        return CreateOrderResponse.builder().status(true).message("Order has been successfully created").build();
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

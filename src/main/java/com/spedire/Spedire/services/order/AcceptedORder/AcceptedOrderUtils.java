package com.spedire.Spedire.services.order.AcceptedORder;

import com.spedire.Spedire.dtos.responses.AcceptedOrderResponse;
import com.spedire.Spedire.dtos.responses.AcceptedOrderResponseForSender;
import com.spedire.Spedire.models.Order;

public class AcceptedOrderUtils {

    public static AcceptedOrderResponse convertOrderToOrderResponseForCarrier(Order foundOrder) {
        return AcceptedOrderResponse.builder()
                .senderName(foundOrder.getSenderName())
                .senderPhoneNumber(foundOrder.getSenderPhoneNumber())
                .senderPossibleLocations(foundOrder.getSenderLocation())
                .receiverName(foundOrder.getReceiverName())
                .receiverPhoneNumber(foundOrder.getReceiverPhoneNumber())
                .receiverLocation(foundOrder.getReceiverLocation())
                .itemDescription(foundOrder.getItemDescription())
                .image(foundOrder.getPicture())
                .orderId(foundOrder.getId())
                .build();
    }

    public static AcceptedOrderResponseForSender convertOrderToOrderResponseForSender(Order foundOrder) {
        return AcceptedOrderResponseForSender.builder().carrierImage(foundOrder.getCarrierImage())
                .carrierPhoneNumber(foundOrder.getCarrierPhoneNumber())
                .carrierName(foundOrder.getCarrierFullName())
                .itemDescription(foundOrder.getItemDescription())
                .costOfDelivery(String.valueOf(foundOrder.getPrice())).build();

    }






}

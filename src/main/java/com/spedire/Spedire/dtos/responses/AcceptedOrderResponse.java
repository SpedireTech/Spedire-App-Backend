package com.spedire.Spedire.dtos.responses;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
public class AcceptedOrderResponse {

    private String orderId;
    private String itemType;
    private String itemDescription;
    private String image;
    private String costOfDelivery;
    private String senderName;
    private String senderPhoneNumber;
//    private List<String> senderPossibleLocations;
    private String senderPossibleLocations;
    private String receiverName;
    private String receiverPhoneNumber;
    private String receiverLocation;


}

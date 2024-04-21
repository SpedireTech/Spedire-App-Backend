package com.spedire.Spedire.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateOrderRequest {

    private String senderName;
    private String senderId;
    private String senderLocation;

    private String senderPhoneNumber;

    private String receiverName;

    private String receiverPhoneNumber;

    private String receiverLocation;

    private String itemDescription;

    private String price;

    private String dueDate;

    private String dueTime;

    private String picture;

}

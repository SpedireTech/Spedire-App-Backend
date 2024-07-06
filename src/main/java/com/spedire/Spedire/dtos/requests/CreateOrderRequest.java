package com.spedire.Spedire.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateOrderRequest {

    private String pickUpNote;

    private String senderName;
    private String itemName;
    private String senderId;
    private List<String> senderLocation;

    private String senderPhoneNumber;

    private String receiverName;

    private String receiverPhoneNumber;

    private String receiverLocation;

    private String itemDescription;

    private String price;

     private String dueDate;

    private String dueTime;

    private String picture;

  //  private String token;

}

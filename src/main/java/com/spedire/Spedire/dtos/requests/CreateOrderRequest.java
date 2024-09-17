package com.spedire.Spedire.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class CreateOrderRequest {

    private String pickUpNote;
    private String itemName;

    private String senderLocation;
    private String receiverName;
    private String receiverPhoneNumber;
    private String receiverLocation;

    private String itemValue;
    private String dueDate;
    private String dueTime;
    private String picture;
    private String dropOffNote;
    private String SenderTown;

    private boolean saveSenderAddress;
    private boolean saveReceiverAddress;


}

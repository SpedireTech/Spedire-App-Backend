package com.spedire.Spedire.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarrierListDtoResponse {
    private String orderId;
    private String itemType;
    private String itemDescription;
    private String image;
    private String costOfDelivery;
    private String senderName;
    private String senderPhoneNumber;
    private String senderLocation;
    private String receiverLocation;
}
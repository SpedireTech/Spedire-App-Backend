package com.spedire.Spedire.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderListDtoResponse {
    private String orderId;
    private String itemType;
    private String itemDescription;
    private String image;
    private String costOfDelivery;
    private String senderName;
    private String senderPhoneNumber;
    private List<String> senderLocation;
    private String receiverLocation;
}

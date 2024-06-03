package com.spedire.Spedire.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AcceptedOrderResponseForSender {
    private String carrierName;
    private String carrierPhoneNumber;
    private String orderId;
    private String carrierEmail;
    private String carrierImage;
    private String costOfDelivery;
    private String itemDescription;


}

package com.spedire.Spedire.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CarrierListDtoResponse {
    private String orderId;
    private String dropOffNote;
    private String pickUpNote;
    private String minutesAway;
    private String image;
    private String senderName;
    private String senderPhoneNumber;
    private String senderLocation;
    private String receiverLocation;
}

package com.spedire.Spedire.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Document
@Getter
@Setter

public class Order {
    @Id
    private String id;

    private String itemDescription;

    private String itemName;

    private BigDecimal itemValue;

    private Date dueDate;

    private LocalTime dueTime;

    private String picture;

    private String senderId;

    private String carriedId;

    private String senderName;

//    private List<String> senderLocation;
    private String senderLocation;

    private String receiverLocation;

    private String senderPhoneNumber;

    private String receiverPhoneNumber;

    private String receiverName;

    private String carrierImage;

    private String carrierFullName;

    private String carrierLocation;

    private String carrierPhoneNumber;

    private boolean payState;

    private String pickUpNote;

    private String dropOffNote;

}



package com.spedire.Spedire.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private BigDecimal price;

    private Date dueDate;

    private LocalTime dueTime;

    private String picture;

    private String senderId;

    private String senderName;

    private List<String> senderLocation;

    private String receiverLocation;

    private String senderPhoneNumber;

    private String receiverPhoneNumber;

    private String receiverName;

}



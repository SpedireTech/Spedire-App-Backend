package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@Document(collection = "sender_pool")
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class SenderPool {

    @Id
    private String id;

    private String pickUpNote;
    private String itemName;

    private String senderLocation;
    private String senderTown;
    private String senderName;
    private String senderId;
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

    private LocalDateTime createdAt;

}

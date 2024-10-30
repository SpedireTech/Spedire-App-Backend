package com.spedire.Spedire.models;

import com.spedire.Spedire.enums.OrderStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Delivery {

    @Id
    private String id;
    private String userId;
    private String currentLocation;
    private String destination;
    private String carrierTown;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

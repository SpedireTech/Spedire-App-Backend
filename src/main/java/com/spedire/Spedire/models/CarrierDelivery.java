package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "carrier_delivery")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class CarrierDelivery {

    @Id
    private String id;
    private String currentLocation;
    private String destination;
}

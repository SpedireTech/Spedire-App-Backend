package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class CarrierPool {
    @Id
    private String id;

    private String name;

    private String destination;
    private String currentLocation;
    private String carrierTown;
    private String phoneNumber;
    private String rating;
    private String deliveryCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }


}

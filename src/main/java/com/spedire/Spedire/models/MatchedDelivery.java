package com.spedire.Spedire.models;

import com.spedire.Spedire.dtos.responses.CarrierListDtoResponse;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "matched_deliveries")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class MatchedDelivery {

    @Id
    private String id;
    private String deliveryId;
    private List<CarrierListDtoResponse> matchedOrders;

}

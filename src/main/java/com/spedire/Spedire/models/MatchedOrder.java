package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "matched_order_list")
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class MatchedOrder {

    @Id
    private String id;

    private String orderId;

    private List<Object> matchedCarriers;

}

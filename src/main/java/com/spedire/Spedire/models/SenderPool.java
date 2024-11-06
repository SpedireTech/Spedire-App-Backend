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
    private Order order;

}

package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "saved_address")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class SavedAddress {

    @Id
    private String id;

    private String email;
    private String senderAddress;
    private String receiverAddress;

}

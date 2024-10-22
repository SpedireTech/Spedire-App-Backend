package com.spedire.Spedire.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "id_verification")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class IdVerification {

    @Id
    private String id;
    private String idCard; // Voters Card, International Passport, Driver's Æicense
    private String nin;
    private String bvn;

}

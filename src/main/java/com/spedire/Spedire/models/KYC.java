package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class KYC {

    @Id
    private String id;
    private String picture;
    private IdVerification idVerification;
    private Bank bank;
}

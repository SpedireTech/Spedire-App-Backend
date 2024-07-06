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
public class Bank {

    @Id
    private String id;
    private String accountNumber;
    private String accountName;
    private String bankName;
}

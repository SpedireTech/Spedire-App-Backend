package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Setter
@Getter
@Document
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Wallet {

    private String userId;
    private BigDecimal walletBalance;

}

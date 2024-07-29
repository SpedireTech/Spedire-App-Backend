package com.spedire.Spedire.models;

import com.spedire.Spedire.enums.PaymentStatus;
import com.spedire.Spedire.enums.PaymentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Document
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class OrderPayment {

    @Id
    private String id;

    private String userEmail;

    private String userId;

    private String orderId;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private String transactionReference;

    private BigDecimal amount;

    private String currency;

    private String status;

    private String paymentChannel;

    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    private String gatewayResponse;




}

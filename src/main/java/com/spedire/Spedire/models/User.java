package com.spedire.Spedire.models;


import com.spedire.Spedire.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String password;
    @Indexed(unique = true)
    private String phoneNumber;
    private String profileImage;

    @Indexed(unique = true)
    private String email;

    private boolean otpVerificationStatus;

    private List<Role> roles = new ArrayList<>();

    private LocalDateTime createdAt;


    /**
     * name: String E.g Ikenna.R
     * walletAmount: Big Decimal
     * totalSentItem
     * totalSuccessfulDelivery
     * totalPendingDelivery
     * totalCancelledDelivery
     * openToDeliver: boolean*/

    private Wallet wallet;
    private int totalSentItem;
    private int totalSuccessfulDelivery;
    private int totalPendingDelivery;
    private int totalCancelledDelivery;
    private boolean openToDelivery;

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

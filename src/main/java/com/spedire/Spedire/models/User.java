package com.spedire.Spedire.models;


import com.spedire.Spedire.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String deliveryCount;

    @Indexed(unique = true)
    private String email;
    private String rating;
    private boolean upgraded;

    private boolean otpVerificationStatus;

    private Set<Role> roles = new HashSet<>();


    private LocalDateTime createdAt;
    private KYC kyc;

    private Wallet wallet;
    private int totalSentItem;
    private int totalSuccessfulDelivery;
    private int totalPendingDelivery;
    private int totalCancelledDelivery;
    private boolean openToDelivery;
    private String reviewId;


}

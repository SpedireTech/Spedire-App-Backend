package com.spedire.Spedire.dtos.responses;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDashboardResponse {

    private String fullName;
    private String phoneNumber;
    private String profileImage;
    private String email;
    private String totalSentItem;
    private String totalSuccessfulDelivery;
    private String totalPendingDelivery;
    private String totalCancelledDelivery;
    private String walletBalance;
    private boolean openToDelivery;

}

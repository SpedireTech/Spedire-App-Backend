package com.spedire.Spedire.dtos.requests;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Setter
public class UpgradeRequest {

    private String picture;
    private String idVerification;
    private String nin;
    private String bvn;
    private String accountNumber;
    private String accountName;
    private String bankName;

}

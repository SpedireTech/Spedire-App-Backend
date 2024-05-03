package com.spedire.Spedire.dtos.responses;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String fullName;

    private String phoneNumber;
    private String profileImage;

    private String email;

}

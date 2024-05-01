package com.spedire.Spedire.dtos.responses;
import lombok.*;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Setter
public class UserProfileResponse {

    private String fullName;
    private String password;

    private String phoneNumber;
    private String profileImage;

    private String email;

}

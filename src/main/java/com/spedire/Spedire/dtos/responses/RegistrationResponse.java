package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Setter
public class RegistrationResponse {

    private String token;
    private String pinId;
}

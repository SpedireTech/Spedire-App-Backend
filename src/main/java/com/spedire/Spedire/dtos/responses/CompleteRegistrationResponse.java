package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CompleteRegistrationResponse {

    private String message;
    private boolean success;


}

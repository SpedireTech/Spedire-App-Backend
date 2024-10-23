package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class VerifyPhoneNumberResponse<T> {

    private String message;
    private T data;

}

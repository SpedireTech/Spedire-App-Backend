package com.spedire.Spedire.models;

import lombok.*;

import java.time.LocalTime;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Otp {

    private String code;
    private String phoneNumber;
    private LocalTime time;

}

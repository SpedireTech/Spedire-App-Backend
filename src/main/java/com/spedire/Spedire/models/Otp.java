package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Otp {

    @Id
    private String id;
    private String otpNumber;
    private String phoneNumber;
    private LocalDateTime createdAt;

}

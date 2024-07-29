package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Document
public class Otp {

    @Id
    private String id;
    private String code;
    private String phoneNumber;
    private LocalTime time;

}

package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
@Setter
public class Response {

    private String token;

    private String message;

}

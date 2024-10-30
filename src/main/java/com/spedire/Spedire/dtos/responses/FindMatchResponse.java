package com.spedire.Spedire.dtos.responses;

import lombok.*;

@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class FindMatchResponse<T> {

    private String message;
    private boolean status;
    private T data;

}

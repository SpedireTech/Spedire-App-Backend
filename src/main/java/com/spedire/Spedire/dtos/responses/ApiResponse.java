package com.spedire.Spedire.dtos.responses;


import lombok.*;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse<T> {

    private String message;
    private boolean success;
    private T data;

}

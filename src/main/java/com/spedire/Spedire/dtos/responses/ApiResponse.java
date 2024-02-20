package com.spedire.Spedire.dtos.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse<T> {

    private String message;
    private boolean success;
    private T data;

}

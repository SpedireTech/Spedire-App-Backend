package com.spedire.Spedire.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class CreateOrderResponse<T> {

    private String message;
    private boolean status;
    private T data;

}

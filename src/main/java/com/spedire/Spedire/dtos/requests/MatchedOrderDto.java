package com.spedire.Spedire.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MatchedOrderDto {

    private String destination;
    private ArrayList<String> currentLocation;
    private String token;


}

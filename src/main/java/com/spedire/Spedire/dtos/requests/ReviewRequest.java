package com.spedire.Spedire.dtos.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewRequest {

    int rating;
    String email;
    String comment;

}

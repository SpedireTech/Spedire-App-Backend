package com.spedire.Spedire.dtos.responses;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private String averageRating;
    private String totalRating;

}

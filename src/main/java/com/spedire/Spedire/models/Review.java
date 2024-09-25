package com.spedire.Spedire.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Review {

    @Id
    private String id;
    private String userId;
    private List<Integer> rating;
    private String comment;
    private LocalDate reviewDate;


}

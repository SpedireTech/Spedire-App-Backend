package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.ReviewResponse;
import com.spedire.Spedire.services.review.ReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spedire.Spedire.dtos.requests.ReviewRequest;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RequestMapping("/api/v1/review")
@RestController
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> rateUser(@RequestBody ReviewRequest request) {
        try {
            ReviewResponse reviewResponse = reviewService.addReview(request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder().message("User rated successfully").data(reviewResponse).success(true).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder().message("Error rating user: " + e.getMessage()).success(false).build());
            }
        }


}

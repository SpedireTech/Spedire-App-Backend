package com.spedire.Spedire.services.review;

import com.spedire.Spedire.dtos.requests.ReviewRequest;
import com.spedire.Spedire.dtos.responses.ReviewResponse;

public interface ReviewInterface {

    ReviewResponse addReview(ReviewRequest request);

    int getRating(String id);
}

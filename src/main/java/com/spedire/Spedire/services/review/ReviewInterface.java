package com.spedire.Spedire.services.review;

import com.spedire.Spedire.dtos.requests.ReviewRequest;
import com.spedire.Spedire.dtos.responses.ReviewResponse;
import com.spedire.Spedire.models.Review;

public interface ReviewInterface {

    ReviewResponse rateUser(ReviewRequest request);
}

package com.spedire.Spedire.services.review;

import com.spedire.Spedire.dtos.requests.ReviewRequest;
import com.spedire.Spedire.dtos.responses.ReviewResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.Review;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.ReviewRepository;
import com.spedire.Spedire.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.spedire.Spedire.services.user.UserServiceUtils.INVALID_EMAIL_ADDRESS;

@Service
@Slf4j
public class ReviewService implements ReviewInterface {

    ReviewRepository reviewRepository;
    UserService userService;
    MongoTemplate mongoTemplate;

    public ReviewService(ReviewRepository reviewRepository, UserService userService, MongoTemplate mongoTemplate) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    private void resetMonthlyRatings() {
        Query query = new Query();
        Update update = new Update().set("rating", new ArrayList<>());
        mongoTemplate.updateMulti(query, update, Review.class);
        log.info("Ratings have been reset for a new month.");
    }

    @Override
    public ReviewResponse addReview(ReviewRequest request) {
        User foundUser = userService.findByEmail(request.getEmail()).orElseThrow(() -> new SpedireException(INVALID_EMAIL_ADDRESS));
        Review review;
        if (foundUser.getReviewId() == null) review = createNewReview(request, foundUser);
         else review = reviewRepository.findById(foundUser.getReviewId()).orElseGet(()
                -> createNewReview(request, foundUser));
        review.getRating().add(request.getRating());
        reviewRepository.save(review);
        return buildReviewResponse(review);
    }

    @Override
    public int getRating(String id) {
        Review review = reviewRepository.findById(id).get();
        if (review.getRating().size() != 0) return calculateAverageRating(review.getRating());
        return 0;
    }


    private Review createNewReview(ReviewRequest request, User user) {
        Review newReview = Review.builder().rating(new ArrayList<>(List.of(request.getRating())))
                .comment(request.getComment()).reviewDate(LocalDate.now()).build();
        reviewRepository.save(newReview);
        user.setReviewId(newReview.getId());
        userService.save(user);
        return newReview;
    }

    private ReviewResponse buildReviewResponse(Review review) {
        int averageRating = calculateAverageRating(review.getRating());
        int totalRating = review.getRating().stream().mapToInt(Integer::intValue).sum();
        return ReviewResponse.builder().averageRating(String.valueOf(averageRating))
                .totalRating(String.valueOf(totalRating)).build();
    }


    private int calculateAverageRating(List<Integer> ratings) {
        if (ratings.isEmpty()) return 0;
        int sum = ratings.stream().mapToInt(Integer::intValue).sum();
        return sum / ratings.size();
    }

}

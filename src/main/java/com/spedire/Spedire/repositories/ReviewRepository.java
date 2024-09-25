package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Otp;
import com.spedire.Spedire.models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {



}

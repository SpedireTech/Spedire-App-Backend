package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtpRepository extends MongoRepository<Otp, String> {



}
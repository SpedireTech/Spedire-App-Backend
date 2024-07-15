package com.spedire.Spedire.repositories;

import com.spedire.Spedire.models.SavedAddress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SavedAddressRepository extends MongoRepository<SavedAddress, String> {

    List<SavedAddress> findByEmail(String email);

}

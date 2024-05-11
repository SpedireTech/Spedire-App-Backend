package com.spedire.Spedire.controllers;

import com.spedire.Spedire.dtos.responses.ApiResponse;
import com.spedire.Spedire.dtos.responses.UserProfileResponse;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.services.cache.RedisInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.spedire.Spedire.controllers.Utils.USER_PROFILE;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@RequestMapping("/redis/")
@RestController
public class RedisController {

    private final RedisInterface redisInterface;

    @DeleteMapping("delete")
    public String deleteCachedUser(String email)  {
        redisInterface.deleteUserCache(email);
        return String.format("%s deleted from cache", email);
    }

}

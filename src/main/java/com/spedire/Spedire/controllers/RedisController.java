package com.spedire.Spedire.controllers;

import com.spedire.Spedire.services.cache.RedisInterface;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

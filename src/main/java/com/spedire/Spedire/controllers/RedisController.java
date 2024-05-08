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

//    @DeleteMapping("delete")
//    public ResponseEntity<Obje<?>> deleteCachedUser(String token)  {
//        boolean response = redisInterface.deleteUserData(token);
//        return ApiResponse.builder().message("User deleted").success(response).build();
//    }

}

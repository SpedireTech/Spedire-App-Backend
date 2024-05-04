package com.spedire.Spedire.services.cache;

import com.spedire.Spedire.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedisService implements RedisInterface{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    public void cacheUserData(User user) {
//        String emailKey = "user:" + user.getEmail();
//
//        if (user.getProfileImage() != null) {
//            redisTemplate.opsForHash().put(emailKey, "picture", user.getProfileImage());
//        }
//        redisTemplate.opsForHash().put(emailKey, "fullName", user.getFullName());
//        redisTemplate.opsForHash().put(emailKey, "password", user.getPassword());
//        redisTemplate.opsForHash().put(emailKey, "phoneNumber", user.getPhoneNumber());
//    }


    public void cacheUserData(User user) {
        String emailKey = "user:" + user.getEmail();
        Map<String, String> userData = new HashMap<>();

        if (user.getProfileImage() != null) {
            userData.put("picture", user.getProfileImage());
        }
        if (user.getFullName() != null) {
            userData.put("fullName", user.getFullName());
        }
        if (user.getPassword() != null) {
            userData.put("password", user.getPassword());
        }
        if (user.getPhoneNumber() != null) {
            userData.put("phoneNumber", user.getPhoneNumber());
        }

        if (!userData.isEmpty()) {
            redisTemplate.opsForHash().putAll(emailKey, userData);
        }
    }


//    public User getUserData(String email) {
//        String emailKey = "user:" + email;
//        String fullName = (String) redisTemplate.opsForHash().get(emailKey, "fullName");
//        String password = (String) redisTemplate.opsForHash().get(emailKey, "password");
//        String phoneNumber = (String) redisTemplate.opsForHash().get(emailKey, "phoneNumber");
//        String profilePicture = (String) redisTemplate.opsForHash().get(emailKey, "picture");
//
//        User user = new User();
//        if (profilePicture != null) {
//            user.setProfileImage(profilePicture);
//        }
//        if (fullName != null && password != null && phoneNumber != null) {
//            user.setEmail(email);
//            user.setFullName(fullName);
//            user.setPassword(password);
//            user.setPhoneNumber(phoneNumber);
//            return user;
//        } else {
//            return null;
//        }
//    }


    public User getUserData(String email) {
        String emailKey = "user:" + email;

        // Check if the key exists in Redis
        Boolean exists = redisTemplate.hasKey(emailKey);
        if (exists != null && !exists) {
            return null; // Key doesn't exist, return null or handle missing data
        }

        String fullName = (String) redisTemplate.opsForHash().get(emailKey, "fullName");
        String password = (String) redisTemplate.opsForHash().get(emailKey, "password");
        String phoneNumber = (String) redisTemplate.opsForHash().get(emailKey, "phoneNumber");
        String profilePicture = (String) redisTemplate.opsForHash().get(emailKey, "picture");

        User user = new User();
        if (profilePicture != null) {
            user.setProfileImage(profilePicture);
        }
        if (fullName != null) {
            user.setEmail(email);
            user.setFullName(fullName);
        }
        if (password != null) {
            user.setPassword(password);
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }

        // Check if essential user data is present, return null if any required attribute is missing
        if (user.getEmail() != null && user.getFullName() != null && user.getPassword() != null && user.getPhoneNumber() != null) {
            return user;
        } else {
            return null; // Missing essential user data, return null or handle partial data
        }
    }


    public void deleteUserCache(String email) {
        String emailKey = "user:" + email;
        redisTemplate.delete(emailKey);
    }
}

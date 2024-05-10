package com.spedire.Spedire.services.cache;

import com.spedire.Spedire.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisService implements RedisInterface{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void cacheUserData(User user) throws RedisConnectionFailureException {
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

        System.out.println("User data in redis == " + userData);
        if (!userData.isEmpty()) {
            redisTemplate.opsForHash().putAll(emailKey, userData);
        }
    }


    public User getUserData(String email) throws RedisConnectionFailureException {
        String emailKey = "user:" + email;

        Boolean exists = redisTemplate.hasKey(emailKey);
        if (exists != null && !exists) {
            return null;
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
        return user;
    }


    public void deleteUserCache(String email) throws RedisConnectionFailureException {
        String emailKey = "user:" + email;
        redisTemplate.delete(emailKey);
    }

    @Override
    public boolean isUserExist(String email) throws RedisConnectionFailureException {
        return Boolean.TRUE.equals(redisTemplate.hasKey(email));
    }

    @Override
    public Map<String, Map<String, String>> getAllData() throws RedisConnectionFailureException {
        Map<String, Map<String, String>> allData = new HashMap<>();
        Set<String> keys = redisTemplate.keys("*");
        System.out.println(keys);
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
                if (data != null && !data.isEmpty()) {
                    Map<String, String> stringData = new HashMap<>();
                    for (Map.Entry<Object, Object> entry : data.entrySet()) {
                        stringData.put(entry.getKey().toString(), entry.getValue().toString());
                    }
                    allData.put(key, stringData);
                }
            }
        }
        return allData;
    }

    @Override
    public void deleteAll() throws RedisConnectionFailureException {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }


    @Override
    public long count() throws RedisConnectionFailureException {
        Set<String> keys = redisTemplate.keys("*");
        return keys != null ? keys.size() : 0;
    }



}

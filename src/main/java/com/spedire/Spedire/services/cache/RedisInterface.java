package com.spedire.Spedire.services.cache;

import com.spedire.Spedire.models.User;

import java.util.List;
import java.util.Map;

public interface RedisInterface {

    public void cacheUserData(User user);

    public User getUserData(String email);

    public void deleteUserCache(String email);

    public boolean isUserExist(String email);

    public void deleteAll();

    public Map<String, Map<String, String>> getAllData();

    public long count();

}

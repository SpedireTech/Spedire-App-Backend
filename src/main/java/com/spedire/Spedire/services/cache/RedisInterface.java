package com.spedire.Spedire.services.cache;

import com.spedire.Spedire.models.User;

public interface RedisInterface {

    public void cacheUserData(User user);

    public User getUserData(String email);

    public void deleteUserCache(String email);
}

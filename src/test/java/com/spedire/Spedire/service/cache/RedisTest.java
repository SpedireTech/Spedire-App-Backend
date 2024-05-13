package com.spedire.Spedire.service.cache;

import com.spedire.Spedire.services.cache.RedisInterface;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisInterface redisInterface;

    @Test
    public void getAllDataInRedis() {
        System.out.println(redisInterface.getAllData());
    }

    @Test
    public void deleteByEmailKey() {
        String email = "zainabalayande01@gmail.com";
        redisInterface.deleteUserCache(email);
        assertNull(redisInterface.getUserData(email));
        assert redisInterface.count() == 0;
    }

    @Test
    public void deleteAllUserData() {
        redisInterface.deleteAll();
        assert redisInterface.count() == 0;
    }

    @Test
    public void getCount() {
        System.out.println("Count is: " + redisInterface.count());
    }


}

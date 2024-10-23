package com.spedire.Spedire.configurations;

import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.cache.RedisInterface;
import com.spedire.Spedire.services.email.JavaMailService;
import com.spedire.Spedire.services.user.UserServiceUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;

@Configuration
public class BeanConfig {

    @Value(JWT_SECRET)
    private String secret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secret);
    }

    @Bean
    public UserServiceUtils userServiceUtils(PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                                             UserRepository userRepository, JavaMailService javaMailService, RedisInterface redisInterface) {
        return new UserServiceUtils(secret, jwtUtil, passwordEncoder, userRepository, javaMailService, redisInterface);
    }
}


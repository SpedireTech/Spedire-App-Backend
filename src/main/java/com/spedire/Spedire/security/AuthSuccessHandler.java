package com.spedire.Spedire.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.spedire.Spedire.dtos.requests.CompleteRegistrationRequest;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.services.cache.RedisInterface;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;


@RequiredArgsConstructor
@Slf4j
@Service
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtils;

    private final RedisInterface redisInterface;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User userDetails = (OAuth2User) authentication.getPrincipal();
        log.info("{} this one works too now ", String.valueOf(userDetails));
        String email = (String) userDetails.getAttributes().get("email");
        String firstName = (String) userDetails.getAttributes().get("given_name");
        String lastName = (String) userDetails.getAttributes().get("family_name");
        String picture = (String) userDetails.getAttributes().get("picture");

        var optionalUser = userRepository.findByEmail(email);

        User user;
        if (optionalUser.isEmpty()) {
            // User not found in the database, create a new user object
            user = new User();
            user.setEmail(email);
            user.setFullName(firstName + " " + lastName);
            user.setProfileImage(picture);
        } else {
            // User found in the database, retrieve the user object
            user = optionalUser.get();
        }

        // Cache the user data
        redisInterface.cacheUserData(user);
        System.out.println(redisInterface.getUserData(user.getEmail()));

        if (optionalUser.isEmpty()) {
            // User not found in the database, redirect to verify-number endpoint
            String token = jwtUtils.fetchToken(user.getEmail());
            response.sendRedirect("http://localhost:5173/verify-number?token=" + token + "&oauth=true");
        } else {
            // User found in the database, generate access token and redirect to loginRedirect endpoint
            String accessToken = jwtUtils.generateAccessToken(user.getEmail());
            response.sendRedirect("http://localhost:5173/loginRedirect?token="+accessToken);
        }
    }



//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) throws IOException {
//        OAuth2User userDetails = (OAuth2User) authentication.getPrincipal();
//        log.info("{} this one works too now ", String.valueOf(userDetails));
//        String email = (String) userDetails.getAttributes().get("email");
//        String firstName = (String) userDetails.getAttributes().get("given_name");
//        String lastName = (String) userDetails.getAttributes().get("family_name");
//        String picture = (String)userDetails.getAttributes().get("picture");
//
//        var optionalUser = userRepository.findByEmail(email);
//
//        if (optionalUser.isEmpty()) {
//            User user = new User();
//            user.setEmail(email);
//            user.setFullName(firstName + " " + lastName);
//            user.setProfileImage(picture);
//            redisInterface.cacheUserData(user);
//            System.out.println(redisInterface.getUserData(user.getEmail()));
//            String token = jwtUtils.fetchToken(user.getEmail());
//            response.sendRedirect("http://localhost:5173/verify-number?token=" + token + "&oauth=true");
//        } else {
//            User user = optionalUser.get();
//            String accessToken = jwtUtils.generateAccessToken(user.getEmail());
//            response.sendRedirect("http://localhost:5173/loginRedirect?token="+accessToken);
//        }
//    }




}
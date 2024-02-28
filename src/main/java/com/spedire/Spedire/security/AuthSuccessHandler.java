package com.spedire.Spedire.security;

import com.auth0.jwt.interfaces.DecodedJWT;

import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;


@AllArgsConstructor
@Slf4j
@Service
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User userDetails = (OAuth2User) authentication.getPrincipal();
        String email = (String) userDetails.getAttributes().get("email");
        var optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) response.sendRedirect("http://localhost:3000/verifynumber?token="+email);
        else {
            User user = optionalUser.get();
            String accessToken = jwtUtils.generateAccessToken("");
            response.sendRedirect("http://localhost:3000/loginRedirect?token="+accessToken);
        }
    }



}
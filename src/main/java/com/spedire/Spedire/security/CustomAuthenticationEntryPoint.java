package com.spedire.Spedire.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spedire.Spedire.dtos.responses.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //You need to log in to access this resource
        ApiResponse<?> errorResponse = ApiResponse.builder().success(false).message("Unauthorized! Please login").build();
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(errorResponse);
        response.getOutputStream().write(jsonResponse.getBytes());

    }
}

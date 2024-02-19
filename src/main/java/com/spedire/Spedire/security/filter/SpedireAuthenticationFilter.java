package com.spedire.Spedire.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spedire.Spedire.dtos.requests.LoginRequest;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.security.JwtUtil;
import com.spedire.Spedire.services.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.spedire.Spedire.security.SecurityUtils.BADCREDENTIALSEXCEPTION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
public class SpedireAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private String email;
    private String password;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private String id;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            email = loginRequest.getEmail();
            password = loginRequest.getPassword();
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authenticationResult = authenticationManager.authenticate(authentication);
            String idForContext = extractIdFromEmail(email);
            id = idForContext;
            Authentication authenticationForContext = new UsernamePasswordAuthenticationToken(idForContext, password);
            SecurityContextHolder.getContext().setAuthentication(authenticationForContext);
            return authenticationResult;
        } catch (IOException exception) {
            throw new BadCredentialsException(BADCREDENTIALSEXCEPTION);
        }
    }

    @SneakyThrows
    private String extractIdFromEmail(String email){
        String id = null;
        id = userService.findMemberByMail(email).getId();
        return  id;
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        String accessToken = jwtUtil.generateAccessToken(id);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("access_token", accessToken);
        //  String email = (String) authResult.getPrincipal();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getOutputStream().write(objectMapper.writeValueAsBytes(
                responseData));
    }

}

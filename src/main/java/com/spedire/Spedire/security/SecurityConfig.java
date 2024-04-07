package com.spedire.Spedire.security;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.spedire.Spedire.exceptions.CustomAuthenticationFailureHandler;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.filter.SpedireAuthenticationFilter;
import com.spedire.Spedire.security.filter.SpedireAuthorizationFilter;
import com.spedire.Spedire.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        UsernamePasswordAuthenticationFilter authenticationFilter = new SpedireAuthenticationFilter(authenticationManager,
                objectMapper, null, null, jwtUtil,  null);
        SpedireAuthorizationFilter authorizationFilter = new SpedireAuthorizationFilter(jwtUtil);


        AuthSuccessHandler authSuccessHandler = new AuthSuccessHandler(userRepository, userService, jwtUtil);

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authorizationFilter, SpedireAuthenticationFilter.class)
//                .exceptionHandling(exceptionHandler -> exceptionHandler.authenticationEntryPoint(customAuthenticationFailureHandler::onAuthenticationFailure))
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class).oauth2Login(c -> c.successHandler(authSuccessHandler))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/user/verifyPhoneNumber", "/api/v1/user/complete-registration", "/api/v1/sms/verify-otp", "/api/v1/user/forgotPassword", "/api/v1/user/resetPassword").permitAll()
                        .anyRequest()
                        .authenticated()).build();
    }

}

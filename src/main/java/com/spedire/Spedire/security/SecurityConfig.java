package com.spedire.Spedire.security;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.spedire.Spedire.exceptions.CustomAuthenticationFailureHandler;
import com.spedire.Spedire.enums.Role;
import com.spedire.Spedire.repositories.UserRepository;
import com.spedire.Spedire.security.filter.SpedireAuthenticationFilter;
import com.spedire.Spedire.security.filter.SpedireAuthorizationFilter;
import com.spedire.Spedire.services.cache.RedisInterface;
import com.spedire.Spedire.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;
@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RedisInterface redisInterface;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        UsernamePasswordAuthenticationFilter authenticationFilter = new SpedireAuthenticationFilter(authenticationManager,
                objectMapper, null, null, jwtUtil,  userService);
        SpedireAuthorizationFilter authorizationFilter = new SpedireAuthorizationFilter(jwtUtil);
        AuthSuccessHandler authSuccessHandler = new AuthSuccessHandler(userRepository, jwtUtil, redisInterface);
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authorizationFilter, SpedireAuthenticationFilter.class)
                .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class).oauth2Login(c -> c.successHandler(authSuccessHandler))
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/user/sign-up", "/api/v1/user/testing",
                                "/api/v1/user/verifyPhoneNumber", "/api/v1/sms/verify-otp", "/api/v1/otp/verifyOtp","/login","/api/v1/order/createOrder", "/api/v1/payment", "api/v1/verify/{reference}", "/api/v1/webhook","/api/v1/carrier/upgrade", "/api/v1/carrier/status", "/gs-guide-websocket/**").permitAll()
                        .requestMatchers("/api/v1/order/matchOrder", "/api/v1/address/sender", "/api/v1/address/receiver").authenticated()
                        .requestMatchers("/api/v1/user/forgotPassword", "/api/v1/user/resetPassword", "/api/v1/user/dashboard",
                                "/api/v1/user/deliveryStatus/{status}", "/api/v1/location/nearbyPlaces", "/api/v1/carrier/downgrade" ).hasAnyAuthority(new SimpleGrantedAuthority(Role.SENDER.name()).getAuthority(), new SimpleGrantedAuthority(Role.CARRIER.name()).getAuthority()).anyRequest().authenticated())
                 .exceptionHandling(exceptionHandling -> exceptionHandling
                         .accessDeniedHandler(accessDeniedHandler)
                         .authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }

}

package com.spedire.Spedire.security.filter;

import com.auth0.jwt.interfaces.Claim;
import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.security.EndPointConstant;
import com.spedire.Spedire.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@AllArgsConstructor
public class SpedireAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, RuntimeException {
        boolean isUnAuthorizedPath = EndPointConstant.UNAUTHORIZEDENDPOINTS.contains(request.getServletPath()) &&
                request.getMethod().equals(HttpMethod.POST.name());
        if (isUnAuthorizedPath) filterChain.doFilter(request, response);
        else {
            authorizeRequest(request, response, filterChain);
        }
    }

    @SneakyThrows
    private void authorizeRequest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)  {
        authorize(request);
        filterChain.doFilter(request, response);
    }

    private void authorize(HttpServletRequest request)  {
        String authorization = request.getHeader(AUTHORIZATION);
        String tokenPrefix = "Bearer ";
        boolean isValidAuthorizationHeader = false;
        if (authorization != null && authorization.startsWith(tokenPrefix))
            isValidAuthorizationHeader = true;

        if (isValidAuthorizationHeader) {
            String token = authorization.substring(tokenPrefix.length());
            System.out.println(token + " token from filter");
            authorizeToken(token);
        }
    }

    private void authorizeToken(String token) {
        Map<String, Claim> map = jwtUtil.extractClaimsFromToken(token);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Claim roles = map.get("roles");
        Claim email = map.get("email");

        addClaimToUserAuthorities(authorities, roles);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void addClaimToUserAuthorities(List<SimpleGrantedAuthority> authorities, Claim claim) {
        if (claim != null) {
            for (int i = 0; i < claim.asMap().size(); i++) {
                String role = (String) claim.asMap().get("role" + (i + 1));
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }
        }
    }
}

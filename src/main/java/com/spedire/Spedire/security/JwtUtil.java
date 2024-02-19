package com.spedire.Spedire.security;

import com.spedire.Spedire.exceptions.SpedireException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Map;

import static com.spedire.Spedire.security.SecurityUtils.JWT_SECRET;

@AllArgsConstructor
@Getter
@Slf4j

public class JwtUtil {

    @Value(JWT_SECRET)
    private String secret;

    public Map<String, Claim> extractClaimsFromToken(String token) throws SpedireException {
        DecodedJWT decodedJwt = validateToken(token);
        return decodedJwt.getClaims();
    }

    public DecodedJWT validateToken(String token){
        log.info("This is the token " + token);
        return JWT.require(Algorithm.HMAC512(secret.getBytes()))
                .build().verify(token);
    }

    public DecodedJWT verifyToken(String token) {
        System.out.println("Token 2"+ token);
        Algorithm algorithm = Algorithm.HMAC512(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String generateAccessToken(String id) {
        System.out.println();

        return JWT
                .create()
                .withIssuedAt(Instant.now())
                .withExpiresAt(
                        Instant.now()
                                .plusSeconds(86000L)
                )
                .withClaim("id", id)
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

}

package com.spedire.Spedire.security.manager;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import static com.spedire.Spedire.security.SecurityUtils.BADCREDENTIALSEXCEPTION;


@Component
@AllArgsConstructor
public class SpedireAuthenticationManager implements AuthenticationManager {

    private final AuthenticationProvider authenticationProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication authenticationResult;
        if (authenticationProvider.supports(authentication.getClass())) {
            authenticationResult = authenticationProvider.authenticate(authentication);
            return authenticationResult;
        } else throw new BadCredentialsException(BADCREDENTIALSEXCEPTION);
    }
}

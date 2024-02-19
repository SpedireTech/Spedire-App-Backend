package com.spedire.Spedire.security.user;

import com.spedire.Spedire.exceptions.SpedireException;
import com.spedire.Spedire.repositories.models.User;
import com.spedire.Spedire.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Primary
public class SpedireUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> member = userRepository.findByEmail(username);
        User foundUser = member.orElseThrow(() -> new SpedireException("Member Not Found"));
        UserDetails userDetails = new SpedireUserDetails(foundUser);
        return userDetails;
    }
}

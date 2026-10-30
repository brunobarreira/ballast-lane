package com.ballastlane.pokemon.infrastructure.security;

import com.ballastlane.pokemon.infrastructure.persistence.repository.JpaUserSpringRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JpaUserSpringRepo userRepo;

    public UserDetailsServiceImpl(JpaUserSpringRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var entity = userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new User(
            entity.getUsername(),
            entity.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_" + entity.getRole().name()))
        );
    }
}

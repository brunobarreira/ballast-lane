package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.application.dto.RegisterRequest;
import com.ballastlane.pokemon.domain.entity.User;
import com.ballastlane.pokemon.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already taken: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }

        User user = new User(
            UUID.randomUUID(),
            request.username(),
            request.email(),
            passwordEncoder.encode(request.password()),
            User.Role.USER
        );
        return userRepository.save(user);
    }
}

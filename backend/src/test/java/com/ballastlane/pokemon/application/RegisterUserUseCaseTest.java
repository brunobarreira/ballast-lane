package com.ballastlane.pokemon.application;

import com.ballastlane.pokemon.application.dto.RegisterRequest;
import com.ballastlane.pokemon.application.usecase.RegisterUserUseCase;
import com.ballastlane.pokemon.domain.entity.User;
import com.ballastlane.pokemon.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks RegisterUserUseCase useCase;

    @Test
    void registersNewUser() {
        when(userRepository.existsByUsername("ash")).thenReturn(false);
        when(userRepository.existsByEmail("ash@pokemon.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(new RegisterRequest("ash", "ash@pokemon.com", "password123"));

        assertThat(result.getUsername()).isEqualTo("ash");
        assertThat(result.getRole()).isEqualTo(User.Role.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void throwsWhenUsernameAlreadyTaken() {
        when(userRepository.existsByUsername("ash")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new RegisterRequest("ash", "ash@pokemon.com", "password123")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ash");
    }

    @Test
    void throwsWhenEmailAlreadyRegistered() {
        when(userRepository.existsByUsername("ash")).thenReturn(false);
        when(userRepository.existsByEmail("ash@pokemon.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new RegisterRequest("ash", "ash@pokemon.com", "password123")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ash@pokemon.com");
    }
}

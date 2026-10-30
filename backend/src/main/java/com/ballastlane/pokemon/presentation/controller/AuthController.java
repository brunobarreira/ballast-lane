package com.ballastlane.pokemon.presentation.controller;

import com.ballastlane.pokemon.application.dto.AuthRequest;
import com.ballastlane.pokemon.application.dto.AuthResponse;
import com.ballastlane.pokemon.application.dto.RegisterRequest;
import com.ballastlane.pokemon.application.usecase.RegisterUserUseCase;
import com.ballastlane.pokemon.domain.entity.User;
import com.ballastlane.pokemon.infrastructure.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        registerUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String role = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .map(a -> a.replace("ROLE_", ""))
            .orElse("USER");
        String token = tokenProvider.generateToken(auth.getName(), role);
        return ResponseEntity.ok(new AuthResponse(token, auth.getName(), role));
    }
}

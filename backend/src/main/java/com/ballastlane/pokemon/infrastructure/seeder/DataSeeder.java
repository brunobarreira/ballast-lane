package com.ballastlane.pokemon.infrastructure.seeder;

import com.ballastlane.pokemon.application.usecase.RegisterUserUseCase;
import com.ballastlane.pokemon.application.usecase.SyncPokemonUseCase;
import com.ballastlane.pokemon.application.dto.RegisterRequest;
import com.ballastlane.pokemon.domain.entity.User;
import com.ballastlane.pokemon.domain.repository.UserRepository;
import com.ballastlane.pokemon.infrastructure.persistence.entity.UserJpaEntity;
import com.ballastlane.pokemon.infrastructure.persistence.repository.JpaUserSpringRepo;
import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final List<String> SEED_POKEMON = List.of(
        "bulbasaur", "charmander", "squirtle", "pikachu", "jigglypuff",
        "meowth", "psyduck", "machop", "geodude", "gastly",
        "eevee", "snorlax", "ditto", "magikarp", "lapras",
        "vaporeon", "jolteon", "flareon", "porygon", "articuno"
    );

    private final SyncPokemonUseCase syncPokemonUseCase;
    private final PokemonRepository pokemonRepository;
    private final JpaUserSpringRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(SyncPokemonUseCase syncPokemonUseCase,
                      PokemonRepository pokemonRepository,
                      JpaUserSpringRepo userRepo,
                      PasswordEncoder passwordEncoder) {
        this.syncPokemonUseCase = syncPokemonUseCase;
        this.pokemonRepository = pokemonRepository;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedPokemon();
    }

    private void seedUsers() {
        if (!userRepo.existsByUsername("admin")) {
            UserJpaEntity admin = new UserJpaEntity();
            admin.setId(UUID.randomUUID());
            admin.setUsername("admin");
            admin.setEmail("admin@ballastlane.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(UserJpaEntity.Role.ADMIN);
            userRepo.save(admin);
            log.info("Seeded admin user");
        }
        if (!userRepo.existsByUsername("trainer")) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(UUID.randomUUID());
            user.setUsername("trainer");
            user.setEmail("trainer@ballastlane.com");
            user.setPasswordHash(passwordEncoder.encode("trainer123"));
            user.setRole(UserJpaEntity.Role.USER);
            userRepo.save(user);
            log.info("Seeded trainer user");
        }
    }

    private void seedPokemon() {
        for (String name : SEED_POKEMON) {
            try {
                if (pokemonRepository.findByName(name).isEmpty()) {
                    syncPokemonUseCase.execute(name);
                    log.info("Seeded pokemon: {}", name);
                }
            } catch (Exception e) {
                log.warn("Failed to seed pokemon {}: {}", name, e.getMessage());
            }
        }
    }
}

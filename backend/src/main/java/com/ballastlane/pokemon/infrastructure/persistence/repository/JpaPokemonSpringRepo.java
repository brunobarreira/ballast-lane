package com.ballastlane.pokemon.infrastructure.persistence.repository;

import com.ballastlane.pokemon.infrastructure.persistence.entity.PokemonJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaPokemonSpringRepo extends JpaRepository<PokemonJpaEntity, UUID> {
    Optional<PokemonJpaEntity> findByPokeapiId(Integer pokeapiId);
    Optional<PokemonJpaEntity> findByName(String name);
    boolean existsByPokeapiId(Integer pokeapiId);
}

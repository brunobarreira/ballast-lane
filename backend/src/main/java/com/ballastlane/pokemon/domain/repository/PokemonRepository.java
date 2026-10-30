package com.ballastlane.pokemon.domain.repository;

import com.ballastlane.pokemon.domain.entity.Pokemon;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PokemonRepository {
    Pokemon save(Pokemon pokemon);
    Optional<Pokemon> findById(UUID id);
    Optional<Pokemon> findByPokeapiId(Integer pokeapiId);
    Optional<Pokemon> findByName(String name);
    List<Pokemon> findAll();
    boolean existsByPokeapiId(Integer pokeapiId);
    void deleteById(UUID id);
}

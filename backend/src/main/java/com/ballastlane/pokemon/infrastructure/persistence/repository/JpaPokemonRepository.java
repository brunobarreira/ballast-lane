package com.ballastlane.pokemon.infrastructure.persistence.repository;

import com.ballastlane.pokemon.domain.entity.Pokemon;
import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import com.ballastlane.pokemon.infrastructure.persistence.mapper.PokemonMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPokemonRepository implements PokemonRepository {

    private final JpaPokemonSpringRepo springRepo;
    private final PokemonMapper mapper;

    public JpaPokemonRepository(JpaPokemonSpringRepo springRepo, PokemonMapper mapper) {
        this.springRepo = springRepo;
        this.mapper = mapper;
    }

    @Override
    public Pokemon save(Pokemon pokemon) {
        var entity = mapper.toEntity(pokemon);
        return mapper.toDomain(springRepo.save(entity));
    }

    @Override
    public Optional<Pokemon> findById(UUID id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Pokemon> findByPokeapiId(Integer pokeapiId) {
        return springRepo.findByPokeapiId(pokeapiId).map(mapper::toDomain);
    }

    @Override
    public Optional<Pokemon> findByName(String name) {
        return springRepo.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Pokemon> findAll() {
        return springRepo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByPokeapiId(Integer pokeapiId) {
        return springRepo.existsByPokeapiId(pokeapiId);
    }

    @Override
    public void deleteById(UUID id) {
        springRepo.deleteById(id);
    }
}

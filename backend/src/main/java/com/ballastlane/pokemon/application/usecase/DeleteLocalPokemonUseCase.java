package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import com.ballastlane.pokemon.presentation.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteLocalPokemonUseCase {

    private final PokemonRepository pokemonRepository;

    public DeleteLocalPokemonUseCase(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public void execute(UUID id) {
        if (!pokemonRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Pokemon not found with id: " + id);
        }
        pokemonRepository.deleteById(id);
    }
}

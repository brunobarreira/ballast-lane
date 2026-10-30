package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.application.dto.LocalPokemonDto;
import com.ballastlane.pokemon.application.dto.UpdatePokemonRequest;
import com.ballastlane.pokemon.domain.entity.Pokemon;
import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import com.ballastlane.pokemon.presentation.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UpdateLocalPokemonUseCase {

    private final PokemonRepository pokemonRepository;

    public UpdateLocalPokemonUseCase(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public LocalPokemonDto execute(UUID id, UpdatePokemonRequest request) {
        Pokemon pokemon = pokemonRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pokemon not found with id: " + id));

        if (request.localName() != null) pokemon.setLocalName(request.localName());
        if (request.region() != null) pokemon.setRegion(request.region());
        if (request.tags() != null) pokemon.setTags(request.tags());

        Pokemon saved = pokemonRepository.save(pokemon);
        return toDto(saved);
    }

    private LocalPokemonDto toDto(Pokemon p) {
        return new LocalPokemonDto(
            p.getId(), p.getPokeapiId(), p.getName(), p.getLocalName(),
            p.getCategory(), p.getMassHg() != null ? p.getMassHg() : 0,
            p.getSpriteUrl(), p.getRegion(),
            p.getTags() != null ? p.getTags() : List.of(),
            p.getSkills() != null ? p.getSkills() : List.of()
        );
    }
}

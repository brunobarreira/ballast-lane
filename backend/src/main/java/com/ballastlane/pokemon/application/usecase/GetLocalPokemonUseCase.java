package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.application.dto.LocalPokemonDto;
import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetLocalPokemonUseCase {

    private final PokemonRepository pokemonRepository;

    public GetLocalPokemonUseCase(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public List<LocalPokemonDto> execute() {
        return pokemonRepository.findAll().stream().map(p -> new LocalPokemonDto(
            p.getId(), p.getPokeapiId(), p.getName(), p.getLocalName(),
            p.getCategory(), p.getMassHg() != null ? p.getMassHg() : 0,
            p.getSpriteUrl(), p.getRegion(),
            p.getTags() != null ? p.getTags() : List.of(),
            p.getSkills() != null ? p.getSkills() : List.of()
        )).toList();
    }
}

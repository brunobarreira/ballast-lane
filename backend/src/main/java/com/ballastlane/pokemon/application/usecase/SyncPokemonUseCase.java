package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.application.dto.LocalPokemonDto;
import com.ballastlane.pokemon.domain.entity.Pokemon;
import com.ballastlane.pokemon.domain.port.PokeApiPort;
import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SyncPokemonUseCase {

    private final PokeApiPort pokeApiPort;
    private final PokemonRepository pokemonRepository;

    public SyncPokemonUseCase(PokeApiPort pokeApiPort, PokemonRepository pokemonRepository) {
        this.pokeApiPort = pokeApiPort;
        this.pokemonRepository = pokemonRepository;
    }

    public LocalPokemonDto execute(String nameOrId) {
        var listItem = pokeApiPort.getPokemonListItem(nameOrId);

        Optional<Pokemon> existing = pokemonRepository.findByPokeapiId(listItem.id());

        Pokemon pokemon = existing.orElseGet(() -> {
            Pokemon p = new Pokemon();
            p.setId(UUID.randomUUID());
            return p;
        });

        pokemon.setPokeapiId(listItem.id());
        pokemon.setName(listItem.name());
        pokemon.setSpriteUrl(listItem.spriteUrl());
        pokemon.setCategory(listItem.category());
        pokemon.setMassHg(listItem.massHg());
        pokemon.setSkills(listItem.skills());

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

package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.application.dto.PokemonDetailDto;
import com.ballastlane.pokemon.domain.port.PokeApiPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GetPokemonDetailUseCase {

    private final PokeApiPort pokeApiPort;

    public GetPokemonDetailUseCase(PokeApiPort pokeApiPort) {
        this.pokeApiPort = pokeApiPort;
    }

    @Cacheable(value = "pokemon-detail", key = "#nameOrId")
    public PokemonDetailDto execute(String nameOrId) {
        return pokeApiPort.getPokemonDetail(nameOrId);
    }
}

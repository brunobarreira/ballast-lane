package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.application.dto.PagedResult;
import com.ballastlane.pokemon.application.dto.PokemonListItemDto;
import com.ballastlane.pokemon.domain.port.PokeApiPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ListPokemonUseCase {

    private final PokeApiPort pokeApiPort;

    public ListPokemonUseCase(PokeApiPort pokeApiPort) {
        this.pokeApiPort = pokeApiPort;
    }

    @Cacheable(value = "pokemon-list", key = "#page + '-' + #size")
    public PagedResult<PokemonListItemDto> execute(int page, int size) {
        return pokeApiPort.listPokemon(page, size);
    }
}

package com.ballastlane.pokemon.domain.port;

import com.ballastlane.pokemon.application.dto.PokemonDetailDto;
import com.ballastlane.pokemon.application.dto.PokemonListItemDto;
import com.ballastlane.pokemon.application.dto.PagedResult;

public interface PokeApiPort {
    PagedResult<PokemonListItemDto> listPokemon(int page, int size);
    PokemonDetailDto getPokemonDetail(String nameOrId);
    PokemonListItemDto getPokemonListItem(String nameOrId);
}

package com.ballastlane.pokemon.application;

import com.ballastlane.pokemon.application.dto.PagedResult;
import com.ballastlane.pokemon.application.dto.PokemonListItemDto;
import com.ballastlane.pokemon.application.usecase.ListPokemonUseCase;
import com.ballastlane.pokemon.domain.port.PokeApiPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListPokemonUseCaseTest {

    @Mock
    PokeApiPort pokeApiPort;

    @InjectMocks
    ListPokemonUseCase useCase;

    @Test
    void returnsPagedResultFromPort() {
        var item = new PokemonListItemDto(1, "bulbasaur", "http://sprite", "Seed Pokémon", 69, List.of("overgrow"));
        var expected = new PagedResult<>(List.of(item), 0, 20, 1302L, 66);
        when(pokeApiPort.listPokemon(0, 20)).thenReturn(expected);

        var result = useCase.execute(0, 20);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("bulbasaur");
        verify(pokeApiPort).listPokemon(0, 20);
    }

    @Test
    void delegatesToPort() {
        when(pokeApiPort.listPokemon(1, 10)).thenReturn(new PagedResult<>(List.of(), 1, 10, 0L, 0));

        useCase.execute(1, 10);

        verify(pokeApiPort).listPokemon(1, 10);
    }
}

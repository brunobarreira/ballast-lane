package com.ballastlane.pokemon.application;

import com.ballastlane.pokemon.application.dto.PokemonDetailDto;
import com.ballastlane.pokemon.application.usecase.GetPokemonDetailUseCase;
import com.ballastlane.pokemon.domain.port.PokeApiPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPokemonDetailUseCaseTest {

    @Mock
    PokeApiPort pokeApiPort;

    @InjectMocks
    GetPokemonDetailUseCase useCase;

    @Test
    void returnsPokemonDetail() {
        var detail = new PokemonDetailDto(25, "pikachu", "http://sprite",
            Map.of("hp", 35, "attack", 55), "A wild Pikachu!", List.of("pichu", "pikachu", "raichu"));
        when(pokeApiPort.getPokemonDetail("pikachu")).thenReturn(detail);

        var result = useCase.execute("pikachu");

        assertThat(result.name()).isEqualTo("pikachu");
        assertThat(result.stats()).containsKey("hp");
        assertThat(result.evolutionChain()).containsExactly("pichu", "pikachu", "raichu");
    }

    @Test
    void acceptsNumericId() {
        var detail = new PokemonDetailDto(25, "pikachu", "", Map.of(), "", List.of());
        when(pokeApiPort.getPokemonDetail("25")).thenReturn(detail);

        var result = useCase.execute("25");

        assertThat(result.id()).isEqualTo(25);
    }
}

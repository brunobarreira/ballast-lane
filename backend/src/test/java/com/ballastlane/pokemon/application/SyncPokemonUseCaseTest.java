package com.ballastlane.pokemon.application;

import com.ballastlane.pokemon.application.dto.PokemonListItemDto;
import com.ballastlane.pokemon.application.usecase.SyncPokemonUseCase;
import com.ballastlane.pokemon.domain.entity.Pokemon;
import com.ballastlane.pokemon.domain.port.PokeApiPort;
import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncPokemonUseCaseTest {

    @Mock PokeApiPort pokeApiPort;
    @Mock PokemonRepository pokemonRepository;
    @InjectMocks SyncPokemonUseCase useCase;

    @Test
    void savesNewPokemonWhenNotExists() {
        var listItem = new PokemonListItemDto(1, "bulbasaur", "http://sprite", "Seed Pokémon", 69, List.of("overgrow"));
        when(pokeApiPort.getPokemonListItem("bulbasaur")).thenReturn(listItem);
        when(pokemonRepository.findByPokeapiId(1)).thenReturn(Optional.empty());
        when(pokemonRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute("bulbasaur");

        assertThat(result.name()).isEqualTo("bulbasaur");
        assertThat(result.pokeapiId()).isEqualTo(1);
        verify(pokemonRepository).save(any(Pokemon.class));
    }

    @Test
    void updatesExistingPokemonWhenAlreadySynced() {
        var existing = new Pokemon(UUID.randomUUID(), 1, "bulbasaur", "Bulba-PT", "Seed", 69, "http://s", null, List.of(), List.of());
        var listItem = new PokemonListItemDto(1, "bulbasaur", "http://new-sprite", "Seed Pokémon", 70, List.of("overgrow", "chlorophyll"));
        when(pokeApiPort.getPokemonListItem("bulbasaur")).thenReturn(listItem);
        when(pokemonRepository.findByPokeapiId(1)).thenReturn(Optional.of(existing));
        when(pokemonRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute("bulbasaur");

        // local name preserved, new data from PokeAPI applied
        assertThat(result.localName()).isEqualTo("Bulba-PT");
        assertThat(result.massHg()).isEqualTo(70);

        ArgumentCaptor<Pokemon> captor = ArgumentCaptor.forClass(Pokemon.class);
        verify(pokemonRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(existing.getId());
    }
}

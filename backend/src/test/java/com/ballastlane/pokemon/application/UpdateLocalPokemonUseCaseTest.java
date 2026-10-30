package com.ballastlane.pokemon.application;

import com.ballastlane.pokemon.application.dto.UpdatePokemonRequest;
import com.ballastlane.pokemon.application.usecase.UpdateLocalPokemonUseCase;
import com.ballastlane.pokemon.domain.entity.Pokemon;
import com.ballastlane.pokemon.domain.repository.PokemonRepository;
import com.ballastlane.pokemon.presentation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLocalPokemonUseCaseTest {

    @Mock PokemonRepository pokemonRepository;
    @InjectMocks UpdateLocalPokemonUseCase useCase;

    @Test
    void updatesLocalName() {
        UUID id = UUID.randomUUID();
        var pokemon = new Pokemon(id, 25, "pikachu", null, "Mouse Pokémon", 60, "http://s", null, List.of(), List.of());
        when(pokemonRepository.findById(id)).thenReturn(Optional.of(pokemon));
        when(pokemonRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var request = new UpdatePokemonRequest("Pikachu-PT", "Kanto", List.of("electric", "cute"));
        var result = useCase.execute(id, request);

        assertThat(result.localName()).isEqualTo("Pikachu-PT");
        assertThat(result.region()).isEqualTo("Kanto");
        assertThat(result.tags()).containsExactly("electric", "cute");
    }

    @Test
    void throwsNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(pokemonRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, new UpdatePokemonRequest(null, null, null)))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining(id.toString());
    }

    @Test
    void partialUpdatePreservesExistingFields() {
        UUID id = UUID.randomUUID();
        var pokemon = new Pokemon(id, 1, "bulbasaur", "Bulba-PT", "Seed", 69, "http://s", "Kanto", List.of("old-tag"), List.of());
        when(pokemonRepository.findById(id)).thenReturn(Optional.of(pokemon));
        when(pokemonRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = useCase.execute(id, new UpdatePokemonRequest(null, "Johto", null));

        assertThat(result.localName()).isEqualTo("Bulba-PT");
        assertThat(result.region()).isEqualTo("Johto");
        assertThat(result.tags()).containsExactly("old-tag");
    }
}

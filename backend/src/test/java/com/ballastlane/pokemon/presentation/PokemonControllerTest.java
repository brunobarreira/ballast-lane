package com.ballastlane.pokemon.presentation;

import com.ballastlane.pokemon.application.dto.PagedResult;
import com.ballastlane.pokemon.application.dto.PokemonDetailDto;
import com.ballastlane.pokemon.application.dto.PokemonListItemDto;
import com.ballastlane.pokemon.application.usecase.GetPokemonDetailUseCase;
import com.ballastlane.pokemon.application.usecase.ListPokemonUseCase;
import com.ballastlane.pokemon.presentation.controller.PokemonController;
import com.ballastlane.pokemon.presentation.exception.GlobalExceptionHandler;
import com.ballastlane.pokemon.presentation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PokemonControllerTest {

    MockMvc mockMvc;

    @Mock ListPokemonUseCase listPokemonUseCase;
    @Mock GetPokemonDetailUseCase getPokemonDetailUseCase;
    @InjectMocks PokemonController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void listReturnsPaginatedResults() throws Exception {
        var item = new PokemonListItemDto(1, "bulbasaur", "http://sprite", "Seed Pokémon", 69, List.of("overgrow"));
        when(listPokemonUseCase.execute(0, 20)).thenReturn(new PagedResult<>(List.of(item), 0, 20, 1302L, 66));

        mockMvc.perform(get("/api/pokemon"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("bulbasaur"))
            .andExpect(jsonPath("$.totalElements").value(1302))
            .andExpect(jsonPath("$.content[0].skills[0]").value("overgrow"));
    }

    @Test
    void detailReturnsFullPokemon() throws Exception {
        var detail = new PokemonDetailDto(25, "pikachu", "http://sprite",
            Map.of("hp", 35), "Pikachu description", List.of("pichu", "pikachu", "raichu"));
        when(getPokemonDetailUseCase.execute("pikachu")).thenReturn(detail);

        mockMvc.perform(get("/api/pokemon/pikachu"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("pikachu"))
            .andExpect(jsonPath("$.evolutionChain[0]").value("pichu"));
    }

    @Test
    void detailReturns404WhenNotFound() throws Exception {
        when(getPokemonDetailUseCase.execute("unknown"))
            .thenThrow(new ResourceNotFoundException("Pokemon not found: unknown"));

        mockMvc.perform(get("/api/pokemon/unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listClampsPageSizeTo100() throws Exception {
        when(listPokemonUseCase.execute(0, 100)).thenReturn(new PagedResult<>(List.of(), 0, 100, 0L, 0));

        mockMvc.perform(get("/api/pokemon?size=999"))
            .andExpect(status().isOk());

        verify(listPokemonUseCase).execute(0, 100);
    }
}

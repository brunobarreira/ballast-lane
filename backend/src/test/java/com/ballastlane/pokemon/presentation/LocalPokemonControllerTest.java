package com.ballastlane.pokemon.presentation;

import com.ballastlane.pokemon.application.dto.LocalPokemonDto;
import com.ballastlane.pokemon.application.usecase.DeleteLocalPokemonUseCase;
import com.ballastlane.pokemon.application.usecase.GetLocalPokemonUseCase;
import com.ballastlane.pokemon.application.usecase.SyncPokemonUseCase;
import com.ballastlane.pokemon.application.usecase.UpdateLocalPokemonUseCase;
import com.ballastlane.pokemon.presentation.controller.LocalPokemonController;
import com.ballastlane.pokemon.presentation.exception.GlobalExceptionHandler;
import com.ballastlane.pokemon.presentation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LocalPokemonControllerTest {

    MockMvc mockMvc;

    @Mock SyncPokemonUseCase syncPokemonUseCase;
    @Mock GetLocalPokemonUseCase getLocalPokemonUseCase;
    @Mock UpdateLocalPokemonUseCase updateLocalPokemonUseCase;
    @Mock DeleteLocalPokemonUseCase deleteLocalPokemonUseCase;
    @InjectMocks LocalPokemonController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void syncReturns201() throws Exception {
        var dto = new LocalPokemonDto(UUID.randomUUID(), 1, "bulbasaur", null, "Seed", 69, "http://s", null, List.of(), List.of());
        when(syncPokemonUseCase.execute("bulbasaur")).thenReturn(dto);

        mockMvc.perform(post("/api/pokemon/bulbasaur/sync"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("bulbasaur"));
    }

    @Test
    void listLocalReturnsAllSynced() throws Exception {
        var dto = new LocalPokemonDto(UUID.randomUUID(), 1, "bulbasaur", "Bulba-PT", "Seed", 69, "http://s", "Kanto", List.of("tag"), List.of("overgrow"));
        when(getLocalPokemonUseCase.execute()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pokemon/local"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].localName").value("Bulba-PT"))
            .andExpect(jsonPath("$[0].region").value("Kanto"));
    }

    @Test
    void updateReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        var dto = new LocalPokemonDto(id, 25, "pikachu", "Pikachu-PT", "Mouse", 60, "http://s", "Kanto", List.of("electric"), List.of());
        when(updateLocalPokemonUseCase.execute(eq(id), any())).thenReturn(dto);

        mockMvc.perform(put("/api/pokemon/local/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"localName\":\"Pikachu-PT\",\"region\":\"Kanto\",\"tags\":[\"electric\"]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.localName").value("Pikachu-PT"));
    }

    @Test
    void updateReturns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(updateLocalPokemonUseCase.execute(eq(id), any()))
            .thenThrow(new ResourceNotFoundException("Pokemon not found with id: " + id));

        mockMvc.perform(put("/api/pokemon/local/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteReturns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteLocalPokemonUseCase).execute(id);

        mockMvc.perform(delete("/api/pokemon/local/" + id))
            .andExpect(status().isNoContent());
    }
}

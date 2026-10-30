package com.ballastlane.pokemon.presentation.controller;

import com.ballastlane.pokemon.application.dto.LocalPokemonDto;
import com.ballastlane.pokemon.application.dto.UpdatePokemonRequest;
import com.ballastlane.pokemon.application.usecase.DeleteLocalPokemonUseCase;
import com.ballastlane.pokemon.application.usecase.GetLocalPokemonUseCase;
import com.ballastlane.pokemon.application.usecase.SyncPokemonUseCase;
import com.ballastlane.pokemon.application.usecase.UpdateLocalPokemonUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pokemon")
public class LocalPokemonController {

    private final SyncPokemonUseCase syncPokemonUseCase;
    private final GetLocalPokemonUseCase getLocalPokemonUseCase;
    private final UpdateLocalPokemonUseCase updateLocalPokemonUseCase;
    private final DeleteLocalPokemonUseCase deleteLocalPokemonUseCase;

    public LocalPokemonController(SyncPokemonUseCase syncPokemonUseCase,
                                  GetLocalPokemonUseCase getLocalPokemonUseCase,
                                  UpdateLocalPokemonUseCase updateLocalPokemonUseCase,
                                  DeleteLocalPokemonUseCase deleteLocalPokemonUseCase) {
        this.syncPokemonUseCase = syncPokemonUseCase;
        this.getLocalPokemonUseCase = getLocalPokemonUseCase;
        this.updateLocalPokemonUseCase = updateLocalPokemonUseCase;
        this.deleteLocalPokemonUseCase = deleteLocalPokemonUseCase;
    }

    @PostMapping("/{nameOrId}/sync")
    public ResponseEntity<LocalPokemonDto> sync(@PathVariable String nameOrId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(syncPokemonUseCase.execute(nameOrId));
    }

    @GetMapping("/local")
    public ResponseEntity<List<LocalPokemonDto>> listLocal() {
        return ResponseEntity.ok(getLocalPokemonUseCase.execute());
    }

    @PutMapping("/local/{id}")
    public ResponseEntity<LocalPokemonDto> update(@PathVariable UUID id,
                                                   @Valid @RequestBody UpdatePokemonRequest request) {
        return ResponseEntity.ok(updateLocalPokemonUseCase.execute(id, request));
    }

    @DeleteMapping("/local/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteLocalPokemonUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

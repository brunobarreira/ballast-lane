package com.ballastlane.pokemon.presentation.controller;

import com.ballastlane.pokemon.application.dto.PagedResult;
import com.ballastlane.pokemon.application.dto.PokemonDetailDto;
import com.ballastlane.pokemon.application.dto.PokemonListItemDto;
import com.ballastlane.pokemon.application.usecase.GetPokemonDetailUseCase;
import com.ballastlane.pokemon.application.usecase.ListPokemonUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final ListPokemonUseCase listPokemonUseCase;
    private final GetPokemonDetailUseCase getPokemonDetailUseCase;

    public PokemonController(ListPokemonUseCase listPokemonUseCase,
                             GetPokemonDetailUseCase getPokemonDetailUseCase) {
        this.listPokemonUseCase = listPokemonUseCase;
        this.getPokemonDetailUseCase = getPokemonDetailUseCase;
    }

    @GetMapping
    public ResponseEntity<PagedResult<PokemonListItemDto>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        if (size > 100) size = 100;
        return ResponseEntity.ok(listPokemonUseCase.execute(page, size));
    }

    @GetMapping("/{nameOrId}")
    public ResponseEntity<PokemonDetailDto> detail(@PathVariable String nameOrId) {
        return ResponseEntity.ok(getPokemonDetailUseCase.execute(nameOrId));
    }
}

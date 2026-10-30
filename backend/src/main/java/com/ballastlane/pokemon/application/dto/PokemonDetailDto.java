package com.ballastlane.pokemon.application.dto;

import java.util.List;
import java.util.Map;

public record PokemonDetailDto(
    int id,
    String name,
    String spriteUrl,
    Map<String, Integer> stats,
    String description,
    List<String> evolutionChain
) {}

package com.ballastlane.pokemon.application.dto;

import java.util.List;

public record PokemonListItemDto(
    int id,
    String name,
    String spriteUrl,
    String category,
    int massHg,
    List<String> skills
) {}

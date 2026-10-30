package com.ballastlane.pokemon.application.dto;

import java.util.List;
import java.util.UUID;

public record LocalPokemonDto(
    UUID id,
    int pokeapiId,
    String name,
    String localName,
    String category,
    int massHg,
    String spriteUrl,
    String region,
    List<String> tags,
    List<String> skills
) {}

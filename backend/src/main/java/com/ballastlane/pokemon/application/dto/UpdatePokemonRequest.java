package com.ballastlane.pokemon.application.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePokemonRequest(
    @Size(max = 100) String localName,
    @Size(max = 100) String region,
    List<@Size(max = 50) String> tags
) {}

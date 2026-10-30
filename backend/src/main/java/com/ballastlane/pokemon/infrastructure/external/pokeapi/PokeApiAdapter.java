package com.ballastlane.pokemon.infrastructure.external.pokeapi;

import com.ballastlane.pokemon.application.dto.PagedResult;
import com.ballastlane.pokemon.application.dto.PokemonDetailDto;
import com.ballastlane.pokemon.application.dto.PokemonListItemDto;
import com.ballastlane.pokemon.domain.port.PokeApiPort;
import com.ballastlane.pokemon.presentation.exception.PokeApiException;
import com.ballastlane.pokemon.presentation.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Component
public class PokeApiAdapter implements PokeApiPort {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PokeApiAdapter(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PagedResult<PokemonListItemDto> listPokemon(int page, int size) {
        int offset = page * size;
        JsonNode listResponse = get("/pokemon?offset=" + offset + "&limit=" + size);

        long total = listResponse.path("count").asLong();
        int totalPages = (int) Math.ceil((double) total / size);

        List<PokemonListItemDto> items = new ArrayList<>();
        for (JsonNode result : listResponse.path("results")) {
            String name = result.path("name").asText();
            try {
                items.add(getPokemonListItem(name));
            } catch (Exception e) {
                // skip individual failures — partial result is better than total failure
            }
        }

        return new PagedResult<>(items, page, size, total, totalPages);
    }

    @Override
    public PokemonListItemDto getPokemonListItem(String nameOrId) {
        JsonNode pokemon = get("/pokemon/" + nameOrId);
        JsonNode species = get("/pokemon-species/" + pokemon.path("id").asInt());

        int id = pokemon.path("id").asInt();
        String name = pokemon.path("name").asText();
        String spriteUrl = pokemon.path("sprites").path("front_default").asText("");
        int massHg = pokemon.path("weight").asInt();

        String category = extractCategory(species);

        List<String> skills = new ArrayList<>();
        for (JsonNode ability : pokemon.path("abilities")) {
            skills.add(ability.path("ability").path("name").asText());
        }

        return new PokemonListItemDto(id, name, spriteUrl, category, massHg, skills);
    }

    @Override
    public PokemonDetailDto getPokemonDetail(String nameOrId) {
        JsonNode pokemon = get("/pokemon/" + nameOrId);
        int id = pokemon.path("id").asInt();
        String name = pokemon.path("name").asText();
        String spriteUrl = pokemon.path("sprites").path("other").path("official-artwork").path("front_default").asText(
            pokemon.path("sprites").path("front_default").asText("")
        );

        Map<String, Integer> stats = new LinkedHashMap<>();
        for (JsonNode stat : pokemon.path("stats")) {
            stats.put(stat.path("stat").path("name").asText(), stat.path("base_stat").asInt());
        }

        JsonNode species = get("/pokemon-species/" + id);
        String description = extractDescription(species);

        String evolutionChainUrl = species.path("evolution_chain").path("url").asText("");
        List<String> evolutionChain = fetchEvolutionChain(evolutionChainUrl);

        return new PokemonDetailDto(id, name, spriteUrl, stats, description, evolutionChain);
    }

    private String extractCategory(JsonNode species) {
        for (JsonNode genus : species.path("genera")) {
            if ("en".equals(genus.path("language").path("name").asText())) {
                return genus.path("genus").asText("");
            }
        }
        return "";
    }

    private String extractDescription(JsonNode species) {
        for (JsonNode entry : species.path("flavor_text_entries")) {
            if ("en".equals(entry.path("language").path("name").asText())) {
                return entry.path("flavor_text").asText("").replace("\n", " ").replace("\f", " ");
            }
        }
        return "";
    }

    private List<String> fetchEvolutionChain(String url) {
        if (url == null || url.isBlank()) return List.of();
        try {
            String path = url.replace("https://pokeapi.co/api/v2", "");
            JsonNode chain = get(path);
            List<String> names = new ArrayList<>();
            collectEvolutions(chain.path("chain"), names);
            return names;
        } catch (Exception e) {
            return List.of();
        }
    }

    private void collectEvolutions(JsonNode chainLink, List<String> names) {
        if (chainLink.isMissingNode()) return;
        names.add(chainLink.path("species").path("name").asText());
        for (JsonNode evolves : chainLink.path("evolves_to")) {
            collectEvolutions(evolves, names);
        }
    }

    private JsonNode get(String path) {
        try {
            String body = webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            return objectMapper.readTree(body);
        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("Pokemon not found: " + path);
        } catch (WebClientResponseException e) {
            throw new PokeApiException("PokeAPI error " + e.getStatusCode() + " for " + path);
        } catch (Exception e) {
            throw new PokeApiException("Failed to reach PokeAPI: " + e.getMessage(), e);
        }
    }
}

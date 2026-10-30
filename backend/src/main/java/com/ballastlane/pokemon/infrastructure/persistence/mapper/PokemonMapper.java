package com.ballastlane.pokemon.infrastructure.persistence.mapper;

import com.ballastlane.pokemon.domain.entity.Pokemon;
import com.ballastlane.pokemon.infrastructure.persistence.entity.PokemonJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PokemonMapper {

    public Pokemon toDomain(PokemonJpaEntity e) {
        return new Pokemon(
            e.getId(), e.getPokeapiId(), e.getName(), e.getLocalName(),
            e.getCategory(), e.getMassHg(), e.getSpriteUrl(), e.getRegion(),
            e.getTags(), e.getSkills()
        );
    }

    public PokemonJpaEntity toEntity(Pokemon p) {
        PokemonJpaEntity e = new PokemonJpaEntity();
        e.setId(p.getId());
        e.setPokeapiId(p.getPokeapiId());
        e.setName(p.getName());
        e.setLocalName(p.getLocalName());
        e.setCategory(p.getCategory());
        e.setMassHg(p.getMassHg());
        e.setSpriteUrl(p.getSpriteUrl());
        e.setRegion(p.getRegion());
        e.setTags(p.getTags());
        e.setSkills(p.getSkills());
        return e;
    }
}

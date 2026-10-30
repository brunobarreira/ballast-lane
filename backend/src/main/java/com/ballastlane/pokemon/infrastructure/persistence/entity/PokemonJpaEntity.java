package com.ballastlane.pokemon.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pokemon")
public class PokemonJpaEntity {

    @Id
    private UUID id;

    @Column(name = "pokeapi_id", unique = true, nullable = false)
    private Integer pokeapiId;

    @Column(nullable = false)
    private String name;

    @Column(name = "local_name")
    private String localName;

    private String category;

    @Column(name = "mass_hg")
    private Integer massHg;

    @Column(name = "sprite_url")
    private String spriteUrl;

    private String region;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    private List<String> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    private List<String> skills;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() { this.updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Integer getPokeapiId() { return pokeapiId; }
    public void setPokeapiId(Integer pokeapiId) { this.pokeapiId = pokeapiId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getMassHg() { return massHg; }
    public void setMassHg(Integer massHg) { this.massHg = massHg; }
    public String getSpriteUrl() { return spriteUrl; }
    public void setSpriteUrl(String spriteUrl) { this.spriteUrl = spriteUrl; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public Instant getUpdatedAt() { return updatedAt; }
}

package com.ballastlane.pokemon.domain.entity;

import java.util.List;
import java.util.UUID;

public class Pokemon {

    private UUID id;
    private Integer pokeapiId;
    private String name;
    private String localName;
    private String category;
    private Integer massHg;
    private String spriteUrl;
    private String region;
    private List<String> tags;
    private List<String> skills;

    public Pokemon() {}

    public Pokemon(UUID id, Integer pokeapiId, String name, String localName, String category,
                   Integer massHg, String spriteUrl, String region, List<String> tags, List<String> skills) {
        this.id = id;
        this.pokeapiId = pokeapiId;
        this.name = name;
        this.localName = localName;
        this.category = category;
        this.massHg = massHg;
        this.spriteUrl = spriteUrl;
        this.region = region;
        this.tags = tags;
        this.skills = skills;
    }

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
}

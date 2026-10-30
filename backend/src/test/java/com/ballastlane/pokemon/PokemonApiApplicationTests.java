package com.ballastlane.pokemon;

import com.ballastlane.pokemon.domain.port.PokeApiPort;
import com.ballastlane.pokemon.infrastructure.seeder.DataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class PokemonApiApplicationTests {

    @MockitoBean
    PokeApiPort pokeApiPort;

    @MockitoBean
    DataSeeder dataSeeder;

    @Test
    void contextLoads() {
    }
}

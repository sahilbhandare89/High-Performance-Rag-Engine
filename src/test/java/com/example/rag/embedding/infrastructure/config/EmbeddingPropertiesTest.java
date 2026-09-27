package com.example.rag.embedding.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableConfigurationProperties(EmbeddingProperties.class)
class EmbeddingPropertiesTest {

    @Autowired
    private EmbeddingProperties properties;

    @Test
    void shouldBindEmbeddingConfiguration() {
        assertThat(properties.getProvider())
                .isEqualTo("fake");

        assertThat(properties.getModel())
                .isEqualTo("fake-v1");

        assertThat(properties.getDimensions())
                .isEqualTo(8);

        assertThat(properties.getBatchSize())
                .isEqualTo(3);

        assertThat(properties.getTimeout())
                .isEqualTo(Duration.ofSeconds(5));
    }
}
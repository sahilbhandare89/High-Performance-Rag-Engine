package com.example.rag.embedding.infrastructure.provider;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.infrastructure.config.EmbeddingProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class EmbeddingProviderFactoryTest {

    @Test
    void shouldCreateLocalProvider() {

        EmbeddingProperties properties = new EmbeddingProperties();

        properties.setProvider("local");
        properties.setDimensions(8);

        EmbeddingProvider provider =
                EmbeddingProviderFactory.create(properties);

        assertThat(provider)
                .isInstanceOf(LocalEmbeddingProvider.class);
    }

    @Test
    void shouldRejectUnknownProvider() {

        EmbeddingProperties properties = new EmbeddingProperties();

        properties.setProvider("unknown");
        properties.setDimensions(8);

        assertThatThrownBy(() ->
                EmbeddingProviderFactory.create(properties)
        )
                .isInstanceOf(IllegalArgumentException.class);
    }
}

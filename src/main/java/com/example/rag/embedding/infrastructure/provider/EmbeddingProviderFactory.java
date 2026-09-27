package com.example.rag.embedding.infrastructure.provider;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.infrastructure.config.EmbeddingProperties;

public final class EmbeddingProviderFactory {

    private EmbeddingProviderFactory() {
    }

    public static EmbeddingProvider create(
            EmbeddingProperties properties
    ) {
        return switch (properties.getProvider().toLowerCase()) {

            case "local" ->
                    new LocalEmbeddingProvider(
                            properties.getDimensions()
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported embedding provider: "
                                    + properties.getProvider()
                    );
        };
    }
}
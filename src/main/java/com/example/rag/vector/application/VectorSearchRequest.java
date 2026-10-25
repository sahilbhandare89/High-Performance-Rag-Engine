package com.example.rag.vector.application;

import com.example.rag.embedding.domain.Embedding;

import java.util.Map;
import java.util.Objects;

public record VectorSearchRequest(
        Embedding queryEmbedding,
        int topK,
        Map<String, String> metadataFilters
) {
    public VectorSearchRequest {
        Objects.requireNonNull(
                queryEmbedding,
                "queryEmbedding must not be null"
        );

        if (topK <= 0) {
            throw new IllegalArgumentException(
                    "topK must be greater than zero"
            );
        }

        metadataFilters = metadataFilters == null
                ? Map.of()
                : Map.copyOf(metadataFilters);
    }

    public VectorSearchRequest(
            Embedding queryEmbedding,
            int topK
    ) {
        this(queryEmbedding, topK, Map.of());
    }
}
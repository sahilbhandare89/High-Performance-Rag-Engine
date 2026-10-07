package com.example.rag.vector.application;

import com.example.rag.embedding.domain.Embedding;

import java.util.Objects;

public record VectorSearchRequest(
        Embedding queryEmbedding,
        int topK
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
    }
}
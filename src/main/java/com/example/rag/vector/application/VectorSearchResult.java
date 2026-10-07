package com.example.rag.vector.application;

import com.example.rag.embedding.domain.VectorRecord;

import java.util.Objects;

public record VectorSearchResult(
        VectorRecord vector,
        double similarity
) {

    public VectorSearchResult {
        Objects.requireNonNull(
                vector,
                "vector must not be null"
        );
    }
}
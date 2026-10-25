
package com.example.rag.retrieval.appication;

import java.util.Map;
import java.util.Objects;

public record SemanticSearchRequest(
        String query,
        int topK,
        Map<String, String> metadataFilters
) {

    public SemanticSearchRequest {
        Objects.requireNonNull(query, "query must not be null");

        if (query.isBlank()) {
            throw new IllegalArgumentException(
                    "query must not be blank"
            );
        }

        if (topK <= 0) {
            throw new IllegalArgumentException(
                    "topK must be greater than zero"
            );
        }

        metadataFilters = metadataFilters == null
                ? Map.of()
                : Map.copyOf(metadataFilters);
    }

    public SemanticSearchRequest(String query, int topK) {
        this(query, topK, Map.of());
    }
}

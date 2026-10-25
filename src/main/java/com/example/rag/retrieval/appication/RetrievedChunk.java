
package com.example.rag.retrieval.appication;

import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentId;

import java.util.Objects;

public record RetrievedChunk(
        DocumentId documentId,
        ChunkId chunkId,
        String content,
        int position,
        double similarity
) {

    public RetrievedChunk {
        Objects.requireNonNull(
                documentId,
                "documentId must not be null"
        );

        Objects.requireNonNull(
                chunkId,
                "chunkId must not be null"
        );

        Objects.requireNonNull(
                content,
                "content must not be null"
        );

        if (content.isBlank()) {
            throw new IllegalArgumentException(
                    "content must not be blank"
            );
        }

        if (position < 0) {
            throw new IllegalArgumentException(
                    "position must not be negative"
            );
        }

        if (!Double.isFinite(similarity)) {
            throw new IllegalArgumentException(
                    "similarity must be finite"
            );
        }
    }
}

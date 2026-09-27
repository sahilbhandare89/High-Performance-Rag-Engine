package com.example.rag.ingestion.domain.document;


import com.example.rag.ingestion.domain.chunking.ChunkId;

import java.util.Objects;

public record DocumentChunk(
        ChunkId id,
        DocumentId documentId,
        String content,
        int position
) {

    public DocumentChunk {
        Objects.requireNonNull(id, "Chunk ID cannot be null");
        Objects.requireNonNull(documentId, "Document ID cannot be null");
        Objects.requireNonNull(content, "Chunk content cannot be null");

        if (content.isEmpty()) {
            throw new IllegalArgumentException(
                    "Chunk content cannot be empty"
            );
        }

        if (position < 0) {
            throw new IllegalArgumentException(
                    "Chunk position cannot be negative"
            );
        }
    }
}
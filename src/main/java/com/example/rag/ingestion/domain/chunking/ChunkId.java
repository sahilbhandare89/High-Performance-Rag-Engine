package com.example.rag.ingestion.domain.chunking;

import java.util.Objects;
import java.util.UUID;

public record ChunkId(UUID value) {

    public ChunkId {
        Objects.requireNonNull(value, "Chunk ID cannot be null");
    }

    public static ChunkId generate() {
        return new ChunkId(UUID.randomUUID());
    }
}
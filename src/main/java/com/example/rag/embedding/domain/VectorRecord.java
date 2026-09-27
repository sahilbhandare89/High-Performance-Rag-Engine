package com.example.rag.embedding.domain;



import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentId;

import java.util.Objects;

public record VectorRecord(
        DocumentId documentId,
        ChunkId chunkId,
        Embedding embedding
) {

    public VectorRecord {
        Objects.requireNonNull(documentId, "documentId must not be null");
        Objects.requireNonNull(chunkId, "chunkId must not be null");
        Objects.requireNonNull(embedding, "embedding must not be null");
    }
}
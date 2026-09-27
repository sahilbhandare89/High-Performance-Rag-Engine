package com.example.rag.embedding.application;


import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.document.DocumentChunk;

import java.util.Objects;

public final class DefaultEmbedDocumentChunkUseCase
        implements EmbedDocumentChunkUseCase {

    private final EmbeddingService embeddingService;

    public DefaultEmbedDocumentChunkUseCase(
            EmbeddingService embeddingService
    ) {
        this.embeddingService =
                Objects.requireNonNull(embeddingService);
    }

    @Override
    public VectorRecord execute(DocumentChunk chunk) {

        Objects.requireNonNull(
                chunk,
                "chunk must not be null"
        );

        Embedding embedding =
                embeddingService.embed(chunk);

        return new VectorRecord(
                chunk.documentId(),
                chunk.id(),
                embedding
        );
    }
}
package com.example.rag.embedding.application;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.ingestion.domain.document.DocumentChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DefaultEmbeddingService
        implements EmbeddingService {

    private final EmbeddingProvider provider;
    private final int batchSize;

    public DefaultEmbeddingService(
            EmbeddingProvider provider,
            int batchSize
    ) {
        this.provider =
                Objects.requireNonNull(provider);

        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "batchSize must be greater than zero"
            );
        }

        this.batchSize = batchSize;
    }

    @Override
    public Embedding embed(DocumentChunk chunk) {
        Objects.requireNonNull(
                chunk,
                "chunk must not be null"
        );

        return provider.embed(chunk.content());
    }

    @Override
    public List<Embedding> embedAll(
            List<DocumentChunk> chunks
    ) {
        Objects.requireNonNull(
                chunks,
                "chunks must not be null"
        );

        if (chunks.isEmpty()) {
            return List.of();
        }

        List<Embedding> result =
                new ArrayList<>(chunks.size());

        for (
                int start = 0;
                start < chunks.size();
                start += batchSize
        ) {
            int end = Math.min(
                    start + batchSize,
                    chunks.size()
            );

            List<DocumentChunk> batch =
                    chunks.subList(start, end);

            List<String> texts =
                    batch.stream()
                            .map(DocumentChunk::content)
                            .toList();

            List<Embedding> embeddings =
                    provider.embedBatch(texts);

            if (embeddings.size() != batch.size()) {
                throw new IllegalStateException(
                        "Provider returned "
                                + embeddings.size()
                                + " embeddings for "
                                + batch.size()
                                + " chunks"
                );
            }

            result.addAll(embeddings);
        }

        return List.copyOf(result);
    }
}
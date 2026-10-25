package com.example.rag.embedding.application;

import com.example.rag.embedding.domain.Embedding;
import com.example.rag.ingestion.domain.document.DocumentChunk;

import java.util.List;

public interface EmbeddingService {

    Embedding embed(DocumentChunk chunk);

    /**
     * Embeds a user search query using the same provider and
     * configuration as chunk embedding.
     */
    Embedding embedQuery(String query);

    List<Embedding> embedAll(List<DocumentChunk> chunks);
}
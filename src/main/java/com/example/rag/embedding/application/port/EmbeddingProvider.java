package com.example.rag.embedding.application.port;

import com.example.rag.embedding.domain.Embedding;

import java.util.List;

public interface EmbeddingProvider {

    Embedding embed(String text);

    /**
     * Generates embeddings in the same order as the input texts.
     *
     * @param texts input texts
     * @return one embedding per input text, preserving order
     */
    List<Embedding> embedBatch(List<String> texts);
}
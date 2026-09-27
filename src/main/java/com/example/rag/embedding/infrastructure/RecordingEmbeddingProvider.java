package com.example.rag.embedding.infrastructure;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.infrastructure.FakeEmbeddingProvider;

import java.util.ArrayList;
import java.util.List;

public final class RecordingEmbeddingProvider
        implements EmbeddingProvider {

    private final List<List<String>> batches =
            new ArrayList<>();

    private final FakeEmbeddingProvider delegate =
            new FakeEmbeddingProvider();

    @Override
    public Embedding embed(String text) {
        return delegate.embed(text);
    }

    @Override
    public List<Embedding> embedBatch(
            List<String> texts
    ) {
        batches.add(List.copyOf(texts));

        return delegate.embedBatch(texts);
    }

    public List<List<String>> batches() {
        return List.copyOf(batches);
    }
}
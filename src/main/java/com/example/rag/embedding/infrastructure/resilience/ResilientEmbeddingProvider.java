package com.example.rag.embedding.infrastructure.resilience;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;

import java.util.List;
import java.util.Objects;

public final class ResilientEmbeddingProvider
        implements EmbeddingProvider {

    private final EmbeddingProvider delegate;
    private final RetryExecutor retryExecutor;
    private final RateLimiter rateLimiter;

    public ResilientEmbeddingProvider(
            EmbeddingProvider delegate,
            RetryExecutor retryExecutor,
            RateLimiter rateLimiter
    ) {
        this.delegate = Objects.requireNonNull(
                delegate,
                "delegate must not be null"
        );

        this.retryExecutor = Objects.requireNonNull(
                retryExecutor,
                "retryExecutor must not be null"
        );

        this.rateLimiter = Objects.requireNonNull(
                rateLimiter,
                "rateLimiter must not be null"
        );
    }

    @Override
    public Embedding embed(String text) {
        return retryExecutor.execute(() -> {

            rateLimiter.acquire();

            return delegate.embed(text);
        });
    }

    @Override
    public List<Embedding> embedBatch(List<String> texts) {
        return retryExecutor.execute(() -> {

            rateLimiter.acquire();

            return delegate.embedBatch(texts);
        });
    }
}
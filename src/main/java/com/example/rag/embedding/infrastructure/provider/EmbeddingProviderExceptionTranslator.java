package com.example.rag.embedding.infrastructure.provider;

import com.example.rag.embedding.application.exception.NonRetryableEmbeddingException;
import com.example.rag.embedding.application.exception.RetryableEmbeddingException;
import com.example.rag.embedding.infrastructure.provider.exception.EmbeddingProviderException;

public final class EmbeddingProviderExceptionTranslator {

    private EmbeddingProviderExceptionTranslator() {
    }

    public static RuntimeException translate(
            EmbeddingProviderException exception
    ) {
        if (isRetryable(exception)) {
            return new RetryableEmbeddingException(
                    exception.getMessage(),
                    exception
            );
        }

        return new NonRetryableEmbeddingException(
                exception.getMessage(),
                exception
        );
    }

    private static boolean isRetryable(
            EmbeddingProviderException exception
    ) {
        // Temporary classification for now.
        return exception.getCause() instanceof java.util.concurrent.TimeoutException;
    }
}
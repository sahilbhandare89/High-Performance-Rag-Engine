package com.example.rag.embedding.infrastructure.resilience;

import com.example.rag.embedding.application.exception.NonRetryableEmbeddingException;
import com.example.rag.embedding.application.exception.RetryableEmbeddingException;

import java.util.Objects;
import java.util.function.Supplier;

public final class RetryExecutor {

    private final RetryPolicy policy;
    private final BackoffStrategy backoffStrategy;
    private final Sleeper sleeper;

    public RetryExecutor(
            RetryPolicy policy,
            BackoffStrategy backoffStrategy,
            Sleeper sleeper
    ) {
        this.policy = Objects.requireNonNull(
                policy,
                "policy must not be null"
        );

        this.backoffStrategy =
                Objects.requireNonNull(
                        backoffStrategy,
                        "backoffStrategy must not be null"
                );

        this.sleeper = Objects.requireNonNull(
                sleeper,
                "sleeper must not be null"
        );
    }

    public <T> T execute(Supplier<T> operation) {

        Objects.requireNonNull(
                operation,
                "operation must not be null"
        );

        RetryableEmbeddingException lastFailure = null;

        for (int attempt = 1;
             attempt <= policy.maxAttempts();
             attempt++) {

            try {
                return operation.get();

            } catch (NonRetryableEmbeddingException e) {
                throw e;

            } catch (RetryableEmbeddingException e) {

                lastFailure = e;

                if (attempt == policy.maxAttempts()) {
                    break;
                }

                sleeper.sleep(
                        backoffStrategy.calculate(attempt)
                );
            }
        }

        throw lastFailure;
    }
}
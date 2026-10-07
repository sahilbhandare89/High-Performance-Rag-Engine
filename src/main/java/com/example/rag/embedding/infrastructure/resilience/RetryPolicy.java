package com.example.rag.embedding.infrastructure.resilience;

import java.time.Duration;
import java.util.Objects;

public record RetryPolicy(
        int maxAttempts,
        Duration initialBackoff,
        Duration maxBackoff
) {

    public RetryPolicy {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(
                    "maxAttempts must be greater than zero"
            );
        }

        Objects.requireNonNull(
                initialBackoff,
                "initialBackoff must not be null"
        );

        Objects.requireNonNull(
                maxBackoff,
                "maxBackoff must not be null"
        );

        if (initialBackoff.isZero()
                || initialBackoff.isNegative()) {
            throw new IllegalArgumentException(
                    "initialBackoff must be positive"
            );
        }

        if (maxBackoff.isZero()
                || maxBackoff.isNegative()) {
            throw new IllegalArgumentException(
                    "maxBackoff must be positive"
            );
        }

        if (maxBackoff.compareTo(initialBackoff) < 0) {
            throw new IllegalArgumentException(
                    "maxBackoff must be >= initialBackoff"
            );
        }
    }
}
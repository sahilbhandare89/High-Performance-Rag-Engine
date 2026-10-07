package com.example.rag.embedding.infrastructure.resilience;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class ExponentialBackoffWithJitter
        implements BackoffStrategy {

    private final Duration initialBackoff;
    private final Duration maxBackoff;
    private final JitterRandom random;

    public ExponentialBackoffWithJitter(
            Duration initialBackoff,
            Duration maxBackoff
    ) {
        this(
                initialBackoff,
                maxBackoff,
                bound ->
                        ThreadLocalRandom.current()
                                .nextLong(bound)
        );
    }

    public ExponentialBackoffWithJitter(
            Duration initialBackoff,
            Duration maxBackoff,
            JitterRandom random
    ) {
        this.initialBackoff =
                Objects.requireNonNull(
                        initialBackoff,
                        "initialBackoff must not be null"
                );

        this.maxBackoff =
                Objects.requireNonNull(
                        maxBackoff,
                        "maxBackoff must not be null"
                );

        this.random =
                Objects.requireNonNull(
                        random,
                        "random must not be null"
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

    @Override
    public Duration calculate(int attempt) {

        if (attempt <= 0) {
            throw new IllegalArgumentException(
                    "attempt must be greater than zero"
            );
        }

        long initialMillis =
                initialBackoff.toMillis();

        long maxMillis =
                maxBackoff.toMillis();

        long multiplier =
                1L << Math.min(attempt - 1, 62);

        long exponentialDelay;

        try {
            exponentialDelay =
                    Math.multiplyExact(
                            initialMillis,
                            multiplier
                    );
        } catch (ArithmeticException e) {
            exponentialDelay = Long.MAX_VALUE;
        }

        long cappedDelay =
                Math.min(
                        exponentialDelay,
                        maxMillis
                );

        if (cappedDelay <= 0) {
            return Duration.ZERO;
        }

        long jitteredDelay =
                random.nextLong(cappedDelay + 1);

        return Duration.ofMillis(jitteredDelay);
    }
}
package com.example.rag.embedding.infrastructure.resilience;

import java.time.Duration;
import java.util.Objects;

public final class FixedRateLimiter implements RateLimiter {

    private final long intervalNanos;

    private long nextAllowedTime;

    public FixedRateLimiter(Duration interval) {

        Objects.requireNonNull(
                interval,
                "interval must not be null"
        );

        if (interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException(
                    "interval must be positive"
            );
        }

        this.intervalNanos = interval.toNanos();
        this.nextAllowedTime = System.nanoTime();
    }

    @Override
    public synchronized void acquire() {

        long now = System.nanoTime();

        if (now < nextAllowedTime) {
            long waitNanos = nextAllowedTime - now;

            try {
                long millis = waitNanos / 1_000_000;
                int nanos =
                        (int) (waitNanos % 1_000_000);

                Thread.sleep(millis, nanos);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new IllegalStateException(
                        "Rate limiter interrupted",
                        e
                );
            }
        }

        nextAllowedTime =
                Math.max(
                        nextAllowedTime,
                        System.nanoTime()
                ) + intervalNanos;
    }
}
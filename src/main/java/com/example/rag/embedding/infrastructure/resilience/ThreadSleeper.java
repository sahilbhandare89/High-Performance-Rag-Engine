package com.example.rag.embedding.infrastructure.resilience;

import java.time.Duration;

public final class ThreadSleeper implements Sleeper {

    @Override
    public void sleep(Duration duration) {
        try {
            Thread.sleep(
                    duration.toMillis(),
                    duration.getNano() % 1_000_000
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Retry interrupted",
                    e
            );
        }
    }
}
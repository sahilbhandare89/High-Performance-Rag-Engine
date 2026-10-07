package com.example.rag.embedding.infrastructure.resilience;


import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class FixedRateLimiterTest {

    @Test
    void shouldAllowFirstAcquireImmediately() {
        FixedRateLimiter limiter =
                new FixedRateLimiter(Duration.ofMillis(100));

        assertDoesNotThrow(limiter::acquire);
    }

    @Test
    void shouldWaitBetweenConsecutiveAcquires() {
        FixedRateLimiter limiter =
                new FixedRateLimiter(Duration.ofMillis(50));

        long start = System.nanoTime();

        limiter.acquire();
        limiter.acquire();

        long elapsedNanos = System.nanoTime() - start;
        long elapsedMillis = elapsedNanos / 1_000_000;

        assertTrue(
                elapsedMillis >= 40,
                "Expected limiter to wait approximately 50ms, but waited "
                        + elapsedMillis + "ms"
        );
    }

    @Test
    void shouldRejectNullInterval() {
        assertThrows(
                NullPointerException.class,
                () -> new FixedRateLimiter(null)
        );
    }

    @Test
    void shouldRejectZeroInterval() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedRateLimiter(Duration.ZERO)
        );
    }

    @Test
    void shouldRejectNegativeInterval() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedRateLimiter(Duration.ofMillis(-1))
        );
    }

    @Test
    void shouldAllowAcquireAfterWaiting() throws InterruptedException {
        FixedRateLimiter limiter =
                new FixedRateLimiter(Duration.ofMillis(30));

        limiter.acquire();

        Thread.sleep(40);

        assertDoesNotThrow(limiter::acquire);
    }
}
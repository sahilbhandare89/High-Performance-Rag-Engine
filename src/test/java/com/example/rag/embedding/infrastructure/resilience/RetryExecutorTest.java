package com.example.rag.embedding.infrastructure.resilience;

import com.example.rag.embedding.application.exception.NonRetryableEmbeddingException;
import com.example.rag.embedding.application.exception.RetryableEmbeddingException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RetryExecutorTest {

    @Test
    void shouldRetryWithExponentialBackoffAndJitter() {

        AtomicInteger attempts = new AtomicInteger();

        List<Duration> sleeps = new ArrayList<>();

        Sleeper sleeper = sleeps::add;

        // Deterministic jitter values:
        // attempt 1 -> 100ms
        // attempt 2 -> 250ms
        JitterRandom random = new JitterRandom() {

            private final long[] values = {
                    100,
                    250
            };

            private int index = 0;

            @Override
            public long nextLong(long bound) {
                return values[index++];
            }
        };

        RetryPolicy policy = new RetryPolicy(
                3,
                Duration.ofMillis(200),
                Duration.ofSeconds(2)
        );

        BackoffStrategy backoffStrategy =
                new ExponentialBackoffWithJitter(
                        policy.initialBackoff(),
                        policy.maxBackoff(),
                        random
                );

        RetryExecutor executor = new RetryExecutor(
                policy,
                backoffStrategy,
                sleeper
        );

        String result = executor.execute(() -> {

            int attempt = attempts.incrementAndGet();

            if (attempt < 3) {
                throw new RetryableEmbeddingException(
                        "temporary failure"
                );
            }

            return "success";
        });

        assertThat(result)
                .isEqualTo("success");

        assertThat(attempts)
                .hasValue(3);

        assertThat(sleeps)
                .containsExactly(
                        Duration.ofMillis(100),
                        Duration.ofMillis(250)
                );
    }

    @Test
    void shouldNotSleepAfterFinalFailedAttempt() {

        AtomicInteger attempts = new AtomicInteger();

        List<Duration> sleeps = new ArrayList<>();

        RetryPolicy policy = new RetryPolicy(
                3,
                Duration.ofMillis(200),
                Duration.ofSeconds(2)
        );

        BackoffStrategy backoffStrategy =
                new ExponentialBackoffWithJitter(
                        policy.initialBackoff(),
                        policy.maxBackoff(),
                        bound -> 100
                );

        RetryExecutor executor = new RetryExecutor(
                policy,
                backoffStrategy,
                sleeps::add
        );

        assertThatThrownBy(() ->
                executor.execute(() -> {
                    attempts.incrementAndGet();

                    throw new RetryableEmbeddingException(
                            "provider unavailable"
                    );
                })
        )
                .isInstanceOf(RetryableEmbeddingException.class);

        assertThat(attempts)
                .hasValue(3);

        // Only attempts 1 and 2 sleep.
        // There is no sleep after attempt 3.
        assertThat(sleeps)
                .hasSize(2);
    }

    @Test
    void shouldNotRetryNonRetryableFailure() {

        AtomicInteger attempts = new AtomicInteger();

        List<Duration> sleeps = new ArrayList<>();

        RetryPolicy policy = new RetryPolicy(
                3,
                Duration.ofMillis(200),
                Duration.ofSeconds(2)
        );

        BackoffStrategy backoffStrategy =
                new ExponentialBackoffWithJitter(
                        policy.initialBackoff(),
                        policy.maxBackoff(),
                        bound -> 100
                );

        RetryExecutor executor = new RetryExecutor(
                policy,
                backoffStrategy,
                sleeps::add
        );

        assertThatThrownBy(() ->
                executor.execute(() -> {
                    attempts.incrementAndGet();

                    throw new NonRetryableEmbeddingException(
                            "invalid request"
                    );
                })
        )
                .isInstanceOf(
                        NonRetryableEmbeddingException.class
                );

        assertThat(attempts)
                .hasValue(1);

        assertThat(sleeps)
                .isEmpty();
    }

    @Test
    void shouldExecuteOnlyOnceWhenOperationSucceeds() {

        AtomicInteger attempts = new AtomicInteger();

        List<Duration> sleeps = new ArrayList<>();

        RetryPolicy policy = new RetryPolicy(
                3,
                Duration.ofMillis(200),
                Duration.ofSeconds(2)
        );

        BackoffStrategy backoffStrategy =
                new ExponentialBackoffWithJitter(
                        policy.initialBackoff(),
                        policy.maxBackoff(),
                        bound -> 100
                );

        RetryExecutor executor = new RetryExecutor(
                policy,
                backoffStrategy,
                sleeps::add
        );

        String result = executor.execute(() -> {
            attempts.incrementAndGet();
            return "success";
        });

        assertThat(result)
                .isEqualTo("success");

        assertThat(attempts)
                .hasValue(1);

        assertThat(sleeps)
                .isEmpty();
    }
}
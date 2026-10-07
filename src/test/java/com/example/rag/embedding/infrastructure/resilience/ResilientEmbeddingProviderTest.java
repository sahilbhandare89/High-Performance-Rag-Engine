package com.example.rag.embedding.infrastructure.resilience;

import com.example.rag.embedding.application.exception.NonRetryableEmbeddingException;
import com.example.rag.embedding.application.exception.RetryableEmbeddingException;
import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ResilientEmbeddingProviderTest {

    @Test
    void shouldCallRateLimiterBeforeSuccessfulEmbed() {

        RecordingRateLimiter rateLimiter =
                new RecordingRateLimiter();

        RecordingEmbeddingProvider provider =
                new RecordingEmbeddingProvider();

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        new RetryPolicy(
                                3,
                                java.time.Duration.ofMillis(1),
                                java.time.Duration.ofMillis(5)
                        ),
                        attempt -> java.time.Duration.ZERO,
                        duration -> {
                        }
                );

        ResilientEmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        Embedding result =
                resilientProvider.embed("hello");

        assertNotNull(result);

        assertEquals(1, provider.embedCalls.get());
        assertEquals(1, rateLimiter.acquireCalls.get());

        assertEquals("hello", provider.lastText);
    }

    @Test
    void shouldCallRateLimiterBeforeSuccessfulBatchEmbed() {

        RecordingRateLimiter rateLimiter =
                new RecordingRateLimiter();

        RecordingEmbeddingProvider provider =
                new RecordingEmbeddingProvider();

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        new RetryPolicy(
                                3,
                                java.time.Duration.ofMillis(1),
                                java.time.Duration.ofMillis(5)
                        ),
                        attempt -> java.time.Duration.ZERO,
                        duration -> {
                        }
                );

        ResilientEmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        List<String> texts =
                List.of("hello", "world");

        List<Embedding> result =
                resilientProvider.embedBatch(texts);

        assertEquals(2, result.size());

        assertEquals(1, provider.batchCalls.get());
        assertEquals(1, rateLimiter.acquireCalls.get());

        assertEquals(texts, provider.lastTexts);
    }

    @Test
    void shouldRateLimitEveryRetryAttempt() {

        RecordingRateLimiter rateLimiter =
                new RecordingRateLimiter();

        FailingThenSuccessfulProvider provider =
                new FailingThenSuccessfulProvider(1);

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        new RetryPolicy(
                                3,
                                java.time.Duration.ofMillis(1),
                                java.time.Duration.ofMillis(5)
                        ),
                        attempt -> java.time.Duration.ZERO,
                        duration -> {
                        }
                );

        ResilientEmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        Embedding result =
                resilientProvider.embed("hello");

        assertNotNull(result);

        assertEquals(2, provider.calls.get());

        // One acquire for each actual provider attempt.
        assertEquals(2, rateLimiter.acquireCalls.get());
    }

    @Test
    void shouldStopAfterMaximumAttempts() {

        RecordingRateLimiter rateLimiter =
                new RecordingRateLimiter();

        AlwaysFailingProvider provider =
                new AlwaysFailingProvider();

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        new RetryPolicy(
                                3,
                                java.time.Duration.ofMillis(1),
                                java.time.Duration.ofMillis(5)
                        ),
                        attempt -> java.time.Duration.ZERO,
                        duration -> {
                        }
                );

        ResilientEmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        assertThrows(
                RetryableEmbeddingException.class,
                () -> resilientProvider.embed("hello")
        );

        assertEquals(3, provider.calls.get());

        // Every attempt must go through the rate limiter.
        assertEquals(3, rateLimiter.acquireCalls.get());
    }

    @Test
    void shouldNotRetryNonRetryableException() {

        RecordingRateLimiter rateLimiter =
                new RecordingRateLimiter();

        NonRetryableFailingProvider provider =
                new NonRetryableFailingProvider();

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        new RetryPolicy(
                                3,
                                java.time.Duration.ofMillis(1),
                                java.time.Duration.ofMillis(5)
                        ),
                        attempt -> java.time.Duration.ZERO,
                        duration -> {
                        }
                );

        ResilientEmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        assertThrows(
                NonRetryableEmbeddingException.class,
                () -> resilientProvider.embed("hello")
        );

        assertEquals(1, provider.calls.get());

        assertEquals(1, rateLimiter.acquireCalls.get());
    }

    // ---------------------------------------------------------
    // Test doubles
    // ---------------------------------------------------------

    private static final class RecordingRateLimiter
            implements RateLimiter {

        private final AtomicInteger acquireCalls =
                new AtomicInteger();

        @Override
        public void acquire() {
            acquireCalls.incrementAndGet();
        }
    }

    private static class RecordingEmbeddingProvider
            implements EmbeddingProvider {

        private final AtomicInteger embedCalls =
                new AtomicInteger();

        private final AtomicInteger batchCalls =
                new AtomicInteger();

        private String lastText;

        private List<String> lastTexts;

        @Override
        public Embedding embed(String text) {

            embedCalls.incrementAndGet();

            lastText = text;

            return new Embedding(
                    new float[]{1.0f, 2.0f, 3.0f}
            );
        }

        @Override
        public List<Embedding> embedBatch(
                List<String> texts
        ) {

            batchCalls.incrementAndGet();

            lastTexts = texts;

            return texts.stream()
                    .map(text ->
                            new Embedding(
                                    new float[]{1.0f, 2.0f, 3.0f}
                            )
                    )
                    .toList();
        }
    }

    private static final class FailingThenSuccessfulProvider
            implements EmbeddingProvider {

        private final int failuresBeforeSuccess;

        private final AtomicInteger calls =
                new AtomicInteger();

        private FailingThenSuccessfulProvider(
                int failuresBeforeSuccess
        ) {
            this.failuresBeforeSuccess =
                    failuresBeforeSuccess;
        }

        @Override
        public Embedding embed(String text) {

            int currentAttempt =
                    calls.incrementAndGet();

            if (currentAttempt <= failuresBeforeSuccess) {
                throw new RetryableEmbeddingException(
                        "Temporary provider failure"
                );
            }

            return new Embedding(
                    new float[]{1.0f, 2.0f, 3.0f}
            );
        }

        @Override
        public List<Embedding> embedBatch(
                List<String> texts
        ) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class AlwaysFailingProvider
            implements EmbeddingProvider {

        private final AtomicInteger calls =
                new AtomicInteger();

        @Override
        public Embedding embed(String text) {

            calls.incrementAndGet();

            throw new RetryableEmbeddingException(
                    "Temporary provider failure"
            );
        }

        @Override
        public List<Embedding> embedBatch(
                List<String> texts
        ) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class NonRetryableFailingProvider
            implements EmbeddingProvider {

        private final AtomicInteger calls =
                new AtomicInteger();

        @Override
        public Embedding embed(String text) {

            calls.incrementAndGet();

            throw new NonRetryableEmbeddingException(
                    "Invalid API request"
            );
        }

        @Override
        public List<Embedding> embedBatch(
                List<String> texts
        ) {
            throw new UnsupportedOperationException();
        }
    }
}
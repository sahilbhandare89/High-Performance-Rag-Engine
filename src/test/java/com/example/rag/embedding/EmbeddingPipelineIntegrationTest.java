package com.example.rag.embedding;

import com.example.rag.embedding.application.DefaultEmbedDocumentChunkUseCase;
import com.example.rag.embedding.application.DefaultEmbeddingService;
import com.example.rag.embedding.application.EmbedDocumentChunkUseCase;
import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.application.exception.NonRetryableEmbeddingException;
import com.example.rag.embedding.application.exception.RetryableEmbeddingException;
import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.embedding.infrastructure.resilience.RateLimiter;
import com.example.rag.embedding.infrastructure.resilience.ResilientEmbeddingProvider;
import com.example.rag.embedding.infrastructure.resilience.RetryExecutor;
import com.example.rag.embedding.infrastructure.resilience.RetryPolicy;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddingPipelineIntegrationTest {

    @Test
    void shouldCreateVectorRecordFromDocumentChunk() {

        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        ChunkId chunkId =
                new ChunkId(UUID.randomUUID());

        DocumentChunk chunk =
                new DocumentChunk(
                        chunkId,
                        documentId,
                        "This is a test document chunk.",
                        0
                );

        EmbeddingProvider provider =
                new FakeEmbeddingProvider();

        RetryPolicy retryPolicy =
                new RetryPolicy(
                        3,
                        Duration.ofMillis(10),
                        Duration.ofMillis(100)
                );

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        retryPolicy,
                        attempt -> Duration.ZERO,
                        duration -> {
                        }
                );

        RateLimiter rateLimiter =
                () -> {
                };

        EmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        EmbeddingService embeddingService =
                new DefaultEmbeddingService(
                        resilientProvider,
                        3
                );

        EmbedDocumentChunkUseCase useCase =
                new DefaultEmbedDocumentChunkUseCase(
                        embeddingService
                );

        VectorRecord result =
                useCase.execute(chunk);

        assertNotNull(result);

        assertEquals(
                documentId,
                result.documentId()
        );

        assertEquals(
                chunkId,
                result.chunkId()
        );

        assertNotNull(
                result.embedding()
        );

        assertEquals(
                8,
                result.embedding().dimensions()
        );
    }

    @Test
    void shouldRetryEmbeddingAndCreateVectorRecord() {

        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        ChunkId chunkId =
                new ChunkId(UUID.randomUUID());

        DocumentChunk chunk =
                new DocumentChunk(
                        chunkId,
                        documentId,
                        "This chunk will fail once before succeeding.",
                        0
                );

        FailingOnceEmbeddingProvider provider =
                new FailingOnceEmbeddingProvider();

        RetryPolicy retryPolicy =
                new RetryPolicy(
                        3,
                        Duration.ofMillis(10),
                        Duration.ofMillis(100)
                );

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        retryPolicy,
                        attempt -> Duration.ZERO,
                        duration -> {
                        }
                );

        RateLimiter rateLimiter =
                () -> {
                };

        EmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        EmbeddingService embeddingService =
                new DefaultEmbeddingService(
                        resilientProvider,
                        3
                );

        EmbedDocumentChunkUseCase useCase =
                new DefaultEmbedDocumentChunkUseCase(
                        embeddingService
                );

        VectorRecord result =
                useCase.execute(chunk);

        assertNotNull(result);

        assertEquals(
                documentId,
                result.documentId()
        );

        assertEquals(
                chunkId,
                result.chunkId()
        );

        assertNotNull(
                result.embedding()
        );

        assertEquals(
                8,
                result.embedding().dimensions()
        );

        assertEquals(
                2,
                provider.calls
        );
    }

    @Test
    void shouldNotRetryNonRetryableEmbeddingFailure() {

        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        ChunkId chunkId =
                new ChunkId(UUID.randomUUID());

        DocumentChunk chunk =
                new DocumentChunk(
                        chunkId,
                        documentId,
                        "This chunk will cause a permanent failure.",
                        0
                );

        NonRetryableEmbeddingProvider provider =
                new NonRetryableEmbeddingProvider();

        RetryPolicy retryPolicy =
                new RetryPolicy(
                        3,
                        Duration.ofMillis(10),
                        Duration.ofMillis(100)
                );

        RetryExecutor retryExecutor =
                new RetryExecutor(
                        retryPolicy,
                        attempt -> Duration.ZERO,
                        duration -> {
                        }
                );

        RateLimiter rateLimiter =
                () -> {
                };

        EmbeddingProvider resilientProvider =
                new ResilientEmbeddingProvider(
                        provider,
                        retryExecutor,
                        rateLimiter
                );

        EmbeddingService embeddingService =
                new DefaultEmbeddingService(
                        resilientProvider,
                        3
                );

        EmbedDocumentChunkUseCase useCase =
                new DefaultEmbedDocumentChunkUseCase(
                        embeddingService
                );

        assertThrows(
                NonRetryableEmbeddingException.class,
                () -> useCase.execute(chunk)
        );

        assertEquals(
                1,
                provider.calls
        );
    }

    private static final class FakeEmbeddingProvider
            implements EmbeddingProvider {

        @Override
        public Embedding embed(String text) {

            return new Embedding(
                    new float[]{
                            0.1f,
                            0.2f,
                            0.3f,
                            0.4f,
                            0.5f,
                            0.6f,
                            0.7f,
                            0.8f
                    }
            );
        }

        @Override
        public List<Embedding> embedBatch(
                List<String> texts
        ) {
            return texts.stream()
                    .map(this::embed)
                    .toList();
        }
    }

    private static final class FailingOnceEmbeddingProvider
            implements EmbeddingProvider {

        private int calls = 0;

        @Override
        public Embedding embed(String text) {

            calls++;

            if (calls == 1) {
                throw new RetryableEmbeddingException(
                        "Temporary provider failure"
                );
            }

            return new Embedding(
                    new float[]{
                            0.1f,
                            0.2f,
                            0.3f,
                            0.4f,
                            0.5f,
                            0.6f,
                            0.7f,
                            0.8f
                    }
            );
        }

        @Override
        public List<Embedding> embedBatch(
                List<String> texts
        ) {
            return texts.stream()
                    .map(this::embed)
                    .toList();
        }
    }

    private static final class NonRetryableEmbeddingProvider
            implements EmbeddingProvider {

        private int calls = 0;

        @Override
        public Embedding embed(String text) {

            calls++;

            throw new NonRetryableEmbeddingException(
                    "Invalid embedding request"
            );
        }

        @Override
        public List<Embedding> embedBatch(
                List<String> texts
        ) {

            return texts.stream()
                    .map(this::embed)
                    .toList();
        }
    }
}
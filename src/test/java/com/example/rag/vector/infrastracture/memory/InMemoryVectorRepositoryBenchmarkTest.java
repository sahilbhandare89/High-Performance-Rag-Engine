package com.example.rag.vector.infrastracture.memory;

import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.vector.application.VectorSearchRequest;
import com.example.rag.vector.application.VectorSearchResult;
import com.example.rag.vector.application.similarity.SimilarityCalculator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class InMemoryVectorRepositoryBenchmarkTest {

    private static final int VECTOR_COUNT = 10_000;
    private static final int DIMENSIONS = 384;
    private static final int TOP_K = 10;
    private static final int WARMUP_ITERATIONS = 5;
    private static final int MEASURED_ITERATIONS = 20;

    @Test
    void benchmarkVectorSearch() {
        InMemoryVectorRepository repository =
                new InMemoryVectorRepository(
                        new SimilarityCalculator(),
                        (documentId, filters) -> true
                );

        DocumentId documentId = DocumentId.generate();
        List<VectorRecord> vectors =
                new ArrayList<>(VECTOR_COUNT);

        for (int i = 0; i < VECTOR_COUNT; i++) {
            float[] values = new float[DIMENSIONS];

            for (int j = 0; j < DIMENSIONS; j++) {
                values[j] = (float) Math.sin(
                        (i + 1.0) * (j + 1.0)
                );
            }

            vectors.add(new VectorRecord(
                    documentId,
                    new ChunkId(UUID.randomUUID()),
                    new Embedding(values)
            ));
        }

        long saveStart = System.nanoTime();
        repository.saveAll(vectors);
        long saveDuration = System.nanoTime() - saveStart;

        float[] queryValues = new float[DIMENSIONS];

        for (int i = 0; i < DIMENSIONS; i++) {
            queryValues[i] = (float) Math.cos(i + 1.0);
        }

        VectorSearchRequest request = new VectorSearchRequest(
                new Embedding(queryValues),
                TOP_K
        );

        // Warm up the JVM before measuring.
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            repository.search(request);
        }

        long totalSearchNanos = 0;

        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
            long start = System.nanoTime();

            List<VectorSearchResult> results =
                    repository.search(request);

            totalSearchNanos += System.nanoTime() - start;

            if (results.size() != TOP_K) {
                throw new AssertionError(
                        "Expected " + TOP_K
                                + " results but got "
                                + results.size()
                );
            }
        }

        double saveMillis = saveDuration / 1_000_000.0;
        double averageSearchMillis =
                totalSearchNanos / (double) MEASURED_ITERATIONS
                        / 1_000_000.0;

        System.out.printf(
                "Vectors: %d, Dimensions: %d, Top-K: %d%n",
                VECTOR_COUNT, DIMENSIONS, TOP_K
        );

        System.out.printf(
                "Bulk save time: %.3f ms%n",
                saveMillis
        );

        System.out.printf(
                "Average search time: %.3f ms%n",
                averageSearchMillis
        );
    }
}
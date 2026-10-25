
package com.example.rag.vector.infrastracture.memory;

import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.vector.application.VectorSearchRequest;
import com.example.rag.vector.application.VectorSearchResult;
import com.example.rag.vector.application.similarity.SimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryVectorRepositorySearchTest {

    private InMemoryVectorRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVectorRepository(
                new SimilarityCalculator(),
                (documentId, filters) -> true
        );
    }

    @Test
    void shouldReturnMostSimilarVectorFirst() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        VectorRecord first = createRecord(
                documentId,
                new float[]{1.0f, 0.0f}
        );

        VectorRecord second = createRecord(
                documentId,
                new float[]{0.0f, 1.0f}
        );

        repository.saveAll(List.of(first, second));

        VectorSearchRequest request =
                new VectorSearchRequest(
                        new Embedding(new float[]{1.0f, 0.0f}),
                        2
                );

        List<VectorSearchResult> results =
                repository.search(request);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).vector()).isEqualTo(first);
        assertThat(results.get(0).similarity()).isEqualTo(1.0);
        assertThat(results.get(1).vector()).isEqualTo(second);
        assertThat(results.get(1).similarity()).isEqualTo(0.0);
    }

    @Test
    void shouldRespectTopK() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        VectorRecord first = createRecord(
                documentId,
                new float[]{1.0f, 0.0f}
        );

        VectorRecord second = createRecord(
                documentId,
                new float[]{0.8f, 0.2f}
        );

        VectorRecord third = createRecord(
                documentId,
                new float[]{0.0f, 1.0f}
        );

        repository.saveAll(List.of(first, second, third));

        VectorSearchRequest request =
                new VectorSearchRequest(
                        new Embedding(new float[]{1.0f, 0.0f}),
                        2
                );

        List<VectorSearchResult> results =
                repository.search(request);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).vector()).isEqualTo(first);
        assertThat(results.get(1).vector()).isEqualTo(second);
    }

    @Test
    void shouldReturnAllVectorsWhenTopKExceedsVectorCount() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        VectorRecord first = createRecord(
                documentId,
                new float[]{1.0f, 0.0f}
        );

        VectorRecord second = createRecord(
                documentId,
                new float[]{0.0f, 1.0f}
        );

        repository.saveAll(List.of(first, second));

        VectorSearchRequest request =
                new VectorSearchRequest(
                        new Embedding(new float[]{1.0f, 0.0f}),
                        10
                );

        List<VectorSearchResult> results =
                repository.search(request);

        assertThat(results).hasSize(2);
    }

    @Test
    void shouldOrderResultsByDescendingSimilarity() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        VectorRecord low = createRecord(
                documentId,
                new float[]{0.0f, 1.0f}
        );

        VectorRecord high = createRecord(
                documentId,
                new float[]{1.0f, 0.0f}
        );

        VectorRecord medium = createRecord(
                documentId,
                new float[]{1.0f, 1.0f}
        );

        repository.saveAll(List.of(low, high, medium));

        VectorSearchRequest request =
                new VectorSearchRequest(
                        new Embedding(new float[]{1.0f, 0.0f}),
                        3
                );

        List<VectorSearchResult> results =
                repository.search(request);

        assertThat(results).hasSize(3);
        assertThat(results.get(0).vector()).isEqualTo(high);
        assertThat(results.get(1).vector()).isEqualTo(medium);
        assertThat(results.get(2).vector()).isEqualTo(low);
    }

    private VectorRecord createRecord(
            DocumentId documentId,
            float[] values
    ) {
        return new VectorRecord(
                documentId,
                new ChunkId(UUID.randomUUID()),
                new Embedding(values)
        );
    }

    @Test
    void shouldFilterResultsByMetadata() {
        DocumentId engineeringDocumentId =
                new DocumentId(UUID.randomUUID());

        DocumentId financeDocumentId =
                new DocumentId(UUID.randomUUID());

        // Only allow documents matching the requested department.
        repository = new InMemoryVectorRepository(
                new SimilarityCalculator(),
                (documentId, filters) ->
                        documentId.equals(engineeringDocumentId)
                                && "engineering".equals(
                                filters.get("department"))
        );

        repository.save(new VectorRecord(
                engineeringDocumentId,
                new ChunkId(UUID.randomUUID()),
                new Embedding(new float[]{1.0f, 0.0f})
        ));

        repository.save(new VectorRecord(
                financeDocumentId,
                new ChunkId(UUID.randomUUID()),
                new Embedding(new float[]{0.99f, 0.01f})
        ));

        VectorSearchRequest request = new VectorSearchRequest(
                new Embedding(new float[]{1.0f, 0.0f}),
                10,
                Map.of("department", "engineering")
        );

        List<VectorSearchResult> results = repository.search(request);

        assertEquals(1, results.size());
        assertEquals(
                engineeringDocumentId,
                results.getFirst().vector().documentId()
        );
    }
}

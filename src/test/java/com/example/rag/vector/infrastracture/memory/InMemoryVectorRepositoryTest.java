//package com.example.rag.vector.infrastracture.memory;
//
//import com.example.rag.embedding.domain.Embedding;
//import com.example.rag.embedding.domain.VectorRecord;
//import com.example.rag.ingestion.domain.chunking.ChunkId;
//import com.example.rag.ingestion.domain.document.DocumentId;
//import com.example.rag.vector.application.VectorSearchRequest;
//import com.example.rag.vector.application.VectorSearchResult;
//import com.example.rag.vector.application.similarity.SimilarityCalculator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.Field;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//class InMemoryVectorRepositoryTest {
//
//    private InMemoryVectorRepository repository;
//
//    @BeforeEach
//    void setUp() {
//        repository = new InMemoryVectorRepository(
//                new SimilarityCalculator()
//        );
//    }
//
//    @Nested
//    @DisplayName("save")
//    class SaveSingleRecord {
//
//        @Test
//        @DisplayName("Should persist vector in internal map and return saved record")
//        void shouldSaveAndReturnVectorRecord() {
//            DocumentId docId = new DocumentId(UUID.randomUUID());
//            ChunkId chunkId = new ChunkId(UUID.randomUUID());
//            VectorRecord record = createRecord(docId, chunkId, new float[]{0.1f, 0.2f});
//
//            VectorRecord saved = repository.save(record);
//
//            assertThat(saved).isEqualTo(record);
//
//            Collection<VectorRecord> savedValues = getInternalMapValues(repository);
//            assertThat(savedValues).hasSize(1);
//        }
//
//        @Test
//        @DisplayName("Should overwrite existing vector record when key matches")
//        void shouldOverwriteRecordWhenKeyMatches() {
//            DocumentId docId = new DocumentId(UUID.randomUUID());
//            ChunkId chunkId = new ChunkId(UUID.randomUUID());
//
//            VectorRecord initialRecord = createRecord(docId, chunkId, new float[]{1.0f, 0.0f});
//            VectorRecord updatedRecord = createRecord(docId, chunkId, new float[]{0.0f, 1.0f});
//
//            repository.save(initialRecord);
//            repository.save(updatedRecord);
//
//            Collection<VectorRecord> savedValues = getInternalMapValues(repository);
//            assertThat(savedValues)
//                    .hasSize(1)
//                    .containsExactly(updatedRecord);
//        }
//    }
//
//    @Nested
//    @DisplayName("saveAll")
//    class SaveMultipleRecords {
//
//        @Test
//        @DisplayName("Should persist all records in collection")
//        void shouldSaveAllRecords() {
//            DocumentId docId = new DocumentId(UUID.randomUUID());
//            VectorRecord record1 = createRecord(docId, new ChunkId(UUID.randomUUID()), new float[]{1.0f, 0.0f});
//            VectorRecord record2 = createRecord(docId, new ChunkId(UUID.randomUUID()), new float[]{0.0f, 1.0f});
//
//            repository.saveAll(List.of(record1, record2));
//
//            Collection<VectorRecord> savedValues = getInternalMapValues(repository);
//            assertThat(savedValues)
//                    .hasSize(2)
//                    .containsExactlyInAnyOrder(record1, record2);
//        }
//
//        @Test
//        @DisplayName("Should handle empty collection without modifying map")
//        void shouldHandleEmptyCollection() {
//            repository.saveAll(Collections.emptyList());
//
//            Collection<VectorRecord> savedValues = getInternalMapValues(repository);
//            assertThat(savedValues).isEmpty();
//        }
//
//        @Test
//        @DisplayName("Should throw NullPointerException when collection is null")
//        @SuppressWarnings("ConstantConditions")
//        void shouldThrowExceptionWhenCollectionIsNull() {
//            Collection<VectorRecord> nullCollection = null;
//
//            assertThatThrownBy(() -> repository.saveAll(nullCollection))
//                    .isInstanceOf(NullPointerException.class);
//        }
//    }
//
//    private VectorRecord createRecord(DocumentId documentId, ChunkId chunkId, float[] values) {
//        return new VectorRecord(documentId, chunkId, new Embedding(values));
//    }
//
//    @SuppressWarnings("unchecked")
//    private Collection<VectorRecord> getInternalMapValues(com.example.rag.vector.infrastructure.memory.InMemoryVectorRepository repo) {
//        try {
//            Field field = com.example.rag.vector.infrastructure.memory.InMemoryVectorRepository.class.getDeclaredField("vectors");
//            field.setAccessible(true);
//            Map<?, VectorRecord> map = (Map<?, VectorRecord>) field.get(repo);
//            return map.values();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to access internal map", e);
//        }
//    }
//
//    @Test
//    void shouldReturnMostSimilarVectorFirst() {
//
//        DocumentId documentId =
//                new DocumentId(UUID.randomUUID());
//
//        VectorRecord first = createRecord(
//                documentId,
//                new ChunkId(UUID.randomUUID()),
//                new float[]{1.0f, 0.0f}
//        );
//
//        VectorRecord second = createRecord(
//                documentId,
//                new ChunkId(UUID.randomUUID()),
//                new float[]{0.0f, 1.0f}
//        );
//
//        repository.saveAll(List.of(first, second));
//
//        VectorSearchRequest request =
//                new VectorSearchRequest(
//                        new Embedding(new float[]{1.0f, 0.0f}),
//                        2
//                );
//
//        List<VectorSearchResult> results =
//                repository.search(request);
//
//        assertThat(results)
//                .hasSize(2);
//
//        assertThat(results.get(0).vector())
//                .isEqualTo(first);
//
//        assertThat(results.get(0).similarity())
//                .isEqualTo(1.0);
//
//        assertThat(results.get(1).vector())
//                .isEqualTo(second);
//
//        assertThat(results.get(1).similarity())
//                .isEqualTo(0.0);
//    }
//}
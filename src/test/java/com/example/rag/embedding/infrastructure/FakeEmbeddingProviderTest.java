package com.example.rag.embedding.infrastructure;

import com.example.rag.embedding.application.DefaultEmbedDocumentChunkUseCase;
import com.example.rag.embedding.application.DefaultEmbeddingService;
import com.example.rag.embedding.application.EmbedDocumentChunkUseCase;
import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class FakeEmbeddingProviderTest {

    @Test
    void shouldProduceDeterministicEmbedding() {

        FakeEmbeddingProvider provider =
                new FakeEmbeddingProvider();

        Embedding first =
                provider.embed("Java virtual threads");

        Embedding second =
                provider.embed("Java virtual threads");

        assertEquals(first, second);
    }

    @Test
    void shouldProduceDifferentEmbeddingForDifferentText() {

        FakeEmbeddingProvider provider =
                new FakeEmbeddingProvider();

        Embedding first =
                provider.embed("Java");

        Embedding second =
                provider.embed("Python");

        assertNotEquals(first, second);
    }

    @Test
    void shouldProduceExpectedDimensions() {

        FakeEmbeddingProvider provider =
                new FakeEmbeddingProvider();

        Embedding embedding =
                provider.embed("Java");

        assertEquals(8, embedding.dimensions());
    }

    @Test
    void shouldEmbedBatch() {

        FakeEmbeddingProvider provider =
                new FakeEmbeddingProvider();

        List<String> texts = List.of(
                "Java",
                "Spring Boot",
                "Kafka"
        );

        List<Embedding> embeddings =
                provider.embedBatch(texts);

        assertEquals(3, embeddings.size());

        assertEquals(8, embeddings.get(0).dimensions());
        assertEquals(8, embeddings.get(1).dimensions());
        assertEquals(8, embeddings.get(2).dimensions());
    }

    @Test
    void shouldReturnEmptyResultForEmptyBatch() {

        FakeEmbeddingProvider provider =
                new FakeEmbeddingProvider();

        List<Embedding> embeddings =
                provider.embedBatch(List.of());

        assertTrue(embeddings.isEmpty());
    }

    @Test
    void shouldSplitChunksIntoBatches() {

        RecordingEmbeddingProvider provider =
                new RecordingEmbeddingProvider();

        EmbeddingService service =
                new DefaultEmbeddingService(
                        provider,
                        3
                );

        List<DocumentChunk> chunks = IntStream.range(0, 7)
                .mapToObj(i -> new DocumentChunk(
                        new ChunkId(UUID.randomUUID()),
                        new DocumentId(UUID.randomUUID()),
                        "Chunk content " + i,
                        i
                ))
                .toList();

        List<Embedding> embeddings =
                service.embedAll(chunks);

        assertEquals(
                7,
                embeddings.size()
        );

        assertEquals(
                3,
                provider.batches().size()
        );

        assertEquals(
                3,
                provider.batches().get(0).size()
        );

        assertEquals(
                3,
                provider.batches().get(1).size()
        );

        assertEquals(
                1,
                provider.batches().get(2).size()
        );
    }

//    @Test
//    void shouldMapChunkToVectorRecord() {
//
//        EmbeddingProvider provider =
//                new FakeEmbeddingProvider();
//
//        EmbeddingService embeddingService =
//                new DefaultEmbeddingService(provider);
//
//        EmbedDocumentChunkUseCase useCase =
//                new DefaultEmbedDocumentChunkUseCase(
//                        embeddingService
//                );
//
//        DocumentChunk chunk = new DocumentChunk(
//                new ChunkId(UUID.randomUUID()),
//                new DocumentId(UUID.randomUUID()),
//                "Java is a programming language.",
//                0
//        );
//
//        VectorRecord result =
//                useCase.execute(chunk);
//
//        assertEquals(
//                chunk.documentId(),
//                result.documentId()
//        );
//
//        assertEquals(
//                chunk.id(),
//                result.chunkId()
//        );
//
//        assertNotNull(result.embedding());
//
//        assertEquals(
//                8,
//                result.embedding().dimensions()
//        );
//    }


}

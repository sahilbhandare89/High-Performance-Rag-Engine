
package com.example.rag.ingestion.application;

import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.DocumentMetadata;
import com.example.rag.ingestion.domain.DocumentStatus;
import com.example.rag.ingestion.domain.chunking.DocumentChunkRepository;
import com.example.rag.ingestion.domain.chunking.DocumentChunker;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.vector.application.VectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentIndexingServiceTest {

    @Mock
    private DocumentChunker documentChunker;

    @Mock
    private DocumentChunkRepository chunkRepository;

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private VectorRepository vectorRepository;

    private DocumentIndexingService indexingService;

    @BeforeEach
    void setUp() {
        indexingService = new DocumentIndexingService(
                documentChunker,
                chunkRepository,
                embeddingService,
                vectorRepository
        );
    }

    @Test
    void shouldCreateAndPersistVectorsForDocumentChunks() {
        Document document = new Document(
                com.example.rag.ingestion.domain.document.DocumentId.generate(),
                "guide.txt",
                "First passage. Second passage.",
                new com.example.rag.ingestion.domain.DocumentMetadata(
                        Map.of()
                ),
                com.example.rag.ingestion.domain.DocumentStatus.RECEIVED,
                Instant.now()
        );

        DocumentChunk first = new DocumentChunk(
                com.example.rag.ingestion.domain.chunking.ChunkId.generate(),
                document.id(),
                "First passage.",
                0
        );

        DocumentChunk second = new DocumentChunk(
                com.example.rag.ingestion.domain.chunking.ChunkId.generate(),
                document.id(),
                "Second passage.",
                1
        );

        Embedding firstEmbedding = new Embedding(
                new float[]{1.0f, 0.0f}
        );

        Embedding secondEmbedding = new Embedding(
                new float[]{0.0f, 1.0f}
        );

        List<DocumentChunk> chunks = List.of(first, second);
        List<Embedding> embeddings =
                List.of(firstEmbedding, secondEmbedding);

        when(documentChunker.chunk(document)).thenReturn(chunks);
        when(embeddingService.embedAll(chunks))
                .thenReturn(embeddings);

        List<VectorRecord> result = indexingService.index(document);

        assertEquals(2, result.size());

        assertEquals(first.id(), result.get(0).chunkId());
        assertSame(firstEmbedding, result.get(0).embedding());

        assertEquals(second.id(), result.get(1).chunkId());
        assertSame(secondEmbedding, result.get(1).embedding());

        InOrder order = inOrder(
                documentChunker,
                embeddingService,
                chunkRepository,
                vectorRepository
        );

        order.verify(documentChunker).chunk(document);
        order.verify(embeddingService).embedAll(chunks);
        order.verify(chunkRepository).saveAll(chunks);
        order.verify(vectorRepository).saveAll(result);
    }


    @Test
    void shouldReturnEmptyListWhenDocumentProducesNoChunks() {
        Document document = new Document(
                DocumentId.generate(),
                "test.txt",
                "Valid document content",
                new DocumentMetadata(Map.of()),
                DocumentStatus.RECEIVED,
                Instant.now()
        );

        when(documentChunker.chunk(document))
                .thenReturn(List.of());

        List<VectorRecord> result = indexingService.index(document);

        assertTrue(result.isEmpty());

        verify(documentChunker).chunk(document);

        verifyNoInteractions(
                embeddingService,
                chunkRepository,
                vectorRepository
        );
    }


    @Test
    void shouldNotPersistChunksWhenEmbeddingFails() {
        Document document = new Document(
                com.example.rag.ingestion.domain.document.DocumentId.generate(),
                "guide.txt",
                "Some content",
                new com.example.rag.ingestion.domain.DocumentMetadata(
                        Map.of()
                ),
                com.example.rag.ingestion.domain.DocumentStatus.RECEIVED,
                Instant.now()
        );

        DocumentChunk chunk = new DocumentChunk(
                com.example.rag.ingestion.domain.chunking.ChunkId.generate(),
                document.id(),
                "Some content",
                0
        );

        when(documentChunker.chunk(document))
                .thenReturn(List.of(chunk));

        when(embeddingService.embedAll(List.of(chunk)))
                .thenThrow(new IllegalStateException("Embedding failed"));

        assertThrows(
                IllegalStateException.class,
                () -> indexingService.index(document)
        );

        verify(chunkRepository, never()).saveAll(anyList());
        verify(vectorRepository, never()).saveAll(anyCollection());
    }

    @Test
    void shouldRejectNullDocument() {
        assertThrows(
                NullPointerException.class,
                () -> indexingService.index(null)
        );

        verifyNoInteractions(
                documentChunker,
                chunkRepository,
                embeddingService,
                vectorRepository
        );
    }
}

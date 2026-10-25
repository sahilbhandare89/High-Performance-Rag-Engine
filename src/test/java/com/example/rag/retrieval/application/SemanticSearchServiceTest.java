
package com.example.rag.retrieval.application;

import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.chunking.DocumentChunkRepository;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.retrieval.appication.RetrievedChunk;
import com.example.rag.retrieval.appication.SemanticSearchRequest;
import com.example.rag.retrieval.appication.SemanticSearchService;
import com.example.rag.vector.application.VectorRepository;
import com.example.rag.vector.application.VectorSearchRequest;
import com.example.rag.vector.application.VectorSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemanticSearchServiceTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private VectorRepository vectorRepository;

    @Mock
    private DocumentChunkRepository chunkRepository;

    @Mock
    private Embedding queryEmbedding;

    private SemanticSearchService semanticSearchService;

    @BeforeEach
    void setUp() {
        semanticSearchService = new SemanticSearchService(
                embeddingService,
                vectorRepository,
                chunkRepository
        );
    }

    @Test
    void shouldEmbedQueryAndReturnMatchingChunk() {
        String query = "How does document ingestion work?";

        DocumentId documentId = DocumentId.generate();
        ChunkId chunkId = ChunkId.generate();

        DocumentChunk chunk = new DocumentChunk(
                chunkId,
                documentId,
                "Document ingestion saves documents.",
                0
        );

        VectorRecord vector = new VectorRecord(
                documentId,
                chunkId,
                queryEmbedding
        );

        VectorSearchResult match =
                new VectorSearchResult(vector, 0.95);

        when(embeddingService.embedQuery(query))
                .thenReturn(queryEmbedding);

        when(vectorRepository.search(any(VectorSearchRequest.class)))
                .thenReturn(List.of(match));

        when(chunkRepository.findByDocumentId(documentId))
                .thenReturn(List.of(chunk));

        SemanticSearchRequest request =
                new SemanticSearchRequest(query, 5);

        List<RetrievedChunk> results =
                semanticSearchService.search(request);

        assertEquals(1, results.size());

        RetrievedChunk result = results.get(0);

        assertEquals(documentId, result.documentId());
        assertEquals(chunkId, result.chunkId());
        assertEquals(
                "Document ingestion saves documents.",
                result.content()
        );
        assertEquals(0, result.position());
        assertEquals(0.95, result.similarity(), 0.000001);

        verify(embeddingService, times(1)).embedQuery(query);
        verify(vectorRepository, times(1))
                .search(any(VectorSearchRequest.class));
    }

    @Test
    void shouldPassTopKAndMetadataFiltersToVectorRepository() {
        String query = "Java embeddings";

        Map<String, String> filters =
                Map.of("category", "technical");

        when(embeddingService.embedQuery(query))
                .thenReturn(queryEmbedding);

        when(vectorRepository.search(any(VectorSearchRequest.class)))
                .thenReturn(List.of());

        SemanticSearchRequest request =
                new SemanticSearchRequest(query, 3, filters);

        List<RetrievedChunk> results =
                semanticSearchService.search(request);

        assertTrue(results.isEmpty());

        ArgumentCaptor<VectorSearchRequest> captor =
                ArgumentCaptor.forClass(VectorSearchRequest.class);

        verify(vectorRepository).search(captor.capture());

        VectorSearchRequest captured = captor.getValue();

        assertSame(queryEmbedding, captured.queryEmbedding());
        assertEquals(3, captured.topK());
        assertEquals(filters, captured.metadataFilters());
    }

    @Test
    void shouldPreserveVectorSearchRanking() {
        DocumentId documentId = DocumentId.generate();

        ChunkId firstId = ChunkId.generate();
        ChunkId secondId = ChunkId.generate();

        DocumentChunk firstChunk = new DocumentChunk(
                firstId, documentId, "Highly relevant passage.", 0
        );

        DocumentChunk secondChunk = new DocumentChunk(
                secondId, documentId, "Less relevant passage.", 1
        );

        VectorSearchResult firstMatch = new VectorSearchResult(
                new VectorRecord(documentId, firstId, queryEmbedding),
                0.95
        );

        VectorSearchResult secondMatch = new VectorSearchResult(
                new VectorRecord(documentId, secondId, queryEmbedding),
                0.75
        );

        when(embeddingService.embedQuery("ranking query"))
                .thenReturn(queryEmbedding);

        when(vectorRepository.search(any(VectorSearchRequest.class)))
                .thenReturn(List.of(firstMatch, secondMatch));

        when(chunkRepository.findByDocumentId(documentId))
                .thenReturn(List.of(firstChunk, secondChunk));

        List<RetrievedChunk> results = semanticSearchService.search(
                new SemanticSearchRequest("ranking query", 2)
        );

        assertEquals(2, results.size());

        assertEquals(firstId, results.get(0).chunkId());
        assertEquals(0.95, results.get(0).similarity(), 0.000001);

        assertEquals(secondId, results.get(1).chunkId());
        assertEquals(0.75, results.get(1).similarity(), 0.000001);
    }

    @Test
    void shouldLoadChunksOnlyOnceForMultipleMatchesFromSameDocument() {
        DocumentId documentId = DocumentId.generate();

        ChunkId firstId = ChunkId.generate();
        ChunkId secondId = ChunkId.generate();

        DocumentChunk firstChunk = new DocumentChunk(
                firstId, documentId, "First passage.", 0
        );

        DocumentChunk secondChunk = new DocumentChunk(
                secondId, documentId, "Second passage.", 1
        );

        when(embeddingService.embedQuery("test query"))
                .thenReturn(queryEmbedding);

        when(vectorRepository.search(any(VectorSearchRequest.class)))
                .thenReturn(List.of(
                        new VectorSearchResult(
                                new VectorRecord(
                                        documentId, firstId, queryEmbedding
                                ),
                                0.9
                        ),
                        new VectorSearchResult(
                                new VectorRecord(
                                        documentId, secondId, queryEmbedding
                                ),
                                0.8
                        )
                ));

        when(chunkRepository.findByDocumentId(documentId))
                .thenReturn(List.of(firstChunk, secondChunk));

        List<RetrievedChunk> results = semanticSearchService.search(
                new SemanticSearchRequest("test query", 2)
        );

        assertEquals(2, results.size());

        verify(chunkRepository, times(1))
                .findByDocumentId(documentId);
    }

    @Test
    void shouldSkipVectorWhenItsSourceChunkIsMissing() {
        DocumentId documentId = DocumentId.generate();
        ChunkId missingChunkId = ChunkId.generate();

        VectorSearchResult match = new VectorSearchResult(
                new VectorRecord(
                        documentId,
                        missingChunkId,
                        queryEmbedding
                ),
                0.9
        );

        when(embeddingService.embedQuery("missing chunk query"))
                .thenReturn(queryEmbedding);

        when(vectorRepository.search(any(VectorSearchRequest.class)))
                .thenReturn(List.of(match));

        when(chunkRepository.findByDocumentId(documentId))
                .thenReturn(List.of());

        List<RetrievedChunk> results = semanticSearchService.search(
                new SemanticSearchRequest("missing chunk query", 5)
        );

        assertTrue(results.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenVectorSearchFindsNothing() {
        when(embeddingService.embedQuery("unknown query"))
                .thenReturn(queryEmbedding);

        when(vectorRepository.search(any(VectorSearchRequest.class)))
                .thenReturn(List.of());

        List<RetrievedChunk> results = semanticSearchService.search(
                new SemanticSearchRequest("unknown query", 5)
        );

        assertTrue(results.isEmpty());

        verifyNoInteractions(chunkRepository);
    }

    @Test
    void shouldRejectNullRequest() {
        assertThrows(
                NullPointerException.class,
                () -> semanticSearchService.search(null)
        );

        verifyNoInteractions(embeddingService);
        verifyNoInteractions(vectorRepository);
        verifyNoInteractions(chunkRepository);
    }


    @Test
    void shouldReturnOnlyTheChunkMatchingTheVectorChunkId() {
        DocumentId documentId = DocumentId.generate();

        ChunkId requestedChunkId = ChunkId.generate();
        ChunkId differentChunkId = ChunkId.generate();

        DocumentChunk differentChunk = new DocumentChunk(
                differentChunkId,
                documentId,
                "This is unrelated content.",
                0
        );

        VectorSearchResult match = new VectorSearchResult(
                new VectorRecord(
                        documentId,
                        requestedChunkId,
                        queryEmbedding
                ),
                0.91
        );

        when(embeddingService.embedQuery("exact chunk query"))
                .thenReturn(queryEmbedding);

        when(vectorRepository.search(any(VectorSearchRequest.class)))
                .thenReturn(List.of(match));

        when(chunkRepository.findByDocumentId(documentId))
                .thenReturn(List.of(differentChunk));

        List<RetrievedChunk> results = semanticSearchService.search(
                new SemanticSearchRequest("exact chunk query", 5)
        );

        assertTrue(results.isEmpty());
    }

}

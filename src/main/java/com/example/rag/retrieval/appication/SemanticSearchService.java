
package com.example.rag.retrieval.appication;

import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.chunking.DocumentChunkRepository;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.vector.application.VectorRepository;
import com.example.rag.vector.application.VectorSearchRequest;
import com.example.rag.vector.application.VectorSearchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public final class SemanticSearchService {

    private final EmbeddingService embeddingService;
    private final VectorRepository vectorRepository;
    private final DocumentChunkRepository chunkRepository;

    public SemanticSearchService(
            EmbeddingService embeddingService,
            VectorRepository vectorRepository,
            DocumentChunkRepository chunkRepository
    ) {
        this.embeddingService = Objects.requireNonNull(
                embeddingService,
                "embeddingService must not be null"
        );
        this.vectorRepository = Objects.requireNonNull(
                vectorRepository,
                "vectorRepository must not be null"
        );
        this.chunkRepository = Objects.requireNonNull(
                chunkRepository,
                "chunkRepository must not be null"
        );
    }

    public List<RetrievedChunk> search(SemanticSearchRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        Embedding queryEmbedding =
                embeddingService.embedQuery(request.query());

        VectorSearchRequest vectorRequest =
                new VectorSearchRequest(
                        queryEmbedding,
                        request.topK(),
                        request.metadataFilters()
                );

        List<VectorSearchResult> matches =
                vectorRepository.search(vectorRequest);

        Map<DocumentId, Map<ChunkId, DocumentChunk>> chunksByDocument =
                new HashMap<>();

        List<RetrievedChunk> retrieved = new ArrayList<>();

        for (VectorSearchResult match : matches) {
            VectorRecord vector = match.vector();

            Map<ChunkId, DocumentChunk> documentChunks =
                    chunksByDocument.computeIfAbsent(
                            vector.documentId(),
                            documentId -> {
                                Map<ChunkId, DocumentChunk> indexed =
                                        new HashMap<>();

                                for (DocumentChunk chunk :
                                        chunkRepository.findByDocumentId(
                                                documentId
                                        )) {
                                    indexed.put(chunk.id(), chunk);
                                }

                                return indexed;
                            }
                    );

            DocumentChunk chunk =
                    documentChunks.get(vector.chunkId());

            // Ignore stale vector records whose source chunk is missing.
            if (chunk == null) {
                continue;
            }

            retrieved.add(
                    new RetrievedChunk(
                            chunk.documentId(),
                            chunk.id(),
                            chunk.content(),
                            chunk.position(),
                            match.similarity()
                    )
            );
        }

        return List.copyOf(retrieved);
    }
}

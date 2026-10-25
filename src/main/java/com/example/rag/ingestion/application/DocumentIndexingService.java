
package com.example.rag.ingestion.application;

import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.domain.Embedding;
import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.chunking.DocumentChunkRepository;
import com.example.rag.ingestion.domain.chunking.DocumentChunker;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.vector.application.VectorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DocumentIndexingService implements DocumentIndexer {

    private final DocumentChunker documentChunker;
    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;
    private final VectorRepository vectorRepository;

    public DocumentIndexingService(
            DocumentChunker documentChunker,
            DocumentChunkRepository chunkRepository,
            EmbeddingService embeddingService,
            VectorRepository vectorRepository
    ) {
        this.documentChunker = Objects.requireNonNull(
                documentChunker,
                "documentChunker must not be null"
        );

        this.chunkRepository = Objects.requireNonNull(
                chunkRepository,
                "chunkRepository must not be null"
        );

        this.embeddingService = Objects.requireNonNull(
                embeddingService,
                "embeddingService must not be null"
        );

        this.vectorRepository = Objects.requireNonNull(
                vectorRepository,
                "vectorRepository must not be null"
        );
    }

    public List<VectorRecord> index(Document document) {
        Objects.requireNonNull(
                document,
                "document must not be null"
        );

        List<DocumentChunk> chunks =
                documentChunker.chunk(document);

        if (chunks.isEmpty()) {
            return List.of();
        }

        List<Embedding> embeddings =
                embeddingService.embedAll(chunks);

        if (embeddings.size() != chunks.size()) {
            throw new IllegalStateException(
                    "Embedding count must match chunk count"
            );
        }

        List<VectorRecord> vectors =
                new ArrayList<>(chunks.size());

        for (int i = 0; i < chunks.size(); i++) {
            DocumentChunk chunk = chunks.get(i);
            Embedding embedding = embeddings.get(i);

            vectors.add(
                    new VectorRecord(
                            chunk.documentId(),
                            chunk.id(),
                            embedding
                    )
            );
        }

        // Persist source text before publishing searchable vectors.
        chunkRepository.saveAll(chunks);
        vectorRepository.saveAll(vectors);

        return List.copyOf(vectors);
    }
}

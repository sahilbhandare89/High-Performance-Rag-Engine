package com.example.rag.ingestion.infrastructure.persistence;

import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.chunking.DocumentChunkRepository;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDocumentChunkRepository
        implements DocumentChunkRepository {

    private final Map<ChunkId, DocumentChunk> chunks =
            new ConcurrentHashMap<>();

    @Override
    public void saveAll(List<DocumentChunk> chunksToSave) {
        Objects.requireNonNull(
                chunksToSave,
                "chunks must not be null"
        );

        for (DocumentChunk chunk : chunksToSave) {
            chunks.put(chunk.id(), chunk);
        }
    }

    @Override
    public List<DocumentChunk> findByDocumentId(DocumentId documentId) {
        Objects.requireNonNull(
                documentId,
                "documentId must not be null"
        );

        return chunks.values().stream()
                .filter(chunk -> chunk.documentId().equals(documentId))
                .sorted(Comparator.comparingInt(DocumentChunk::position))
                .toList();
    }

    @Override
    public Map<ChunkId, DocumentChunk> findAllById(
            Collection<ChunkId> chunkIds
    ) {
        Objects.requireNonNull(
                chunkIds,
                "chunkIds must not be null"
        );

        Map<ChunkId, DocumentChunk> found =
                new HashMap<>();

        for (ChunkId id : chunkIds) {
            DocumentChunk chunk = chunks.get(id);

            if (chunk != null) {
                found.put(id, chunk);
            }
        }

        return found;
    }

    @Override
    public void deleteByDocumentId(DocumentId documentId) {
        Objects.requireNonNull(
                documentId,
                "documentId must not be null"
        );

        chunks.values().removeIf(
                chunk -> chunk.documentId().equals(documentId)
        );
    }
}
package com.example.rag.ingestion.domain.chunking;

import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DocumentChunkRepository {

    void saveAll(List<DocumentChunk> chunks);

    List<DocumentChunk> findByDocumentId(DocumentId documentId);

    /**
     * Batch lookup. IDs that do not exist are simply absent from the
     * returned map. Ordering is the caller's responsibility.
     */
    Map<ChunkId, DocumentChunk> findAllById(Collection<ChunkId> chunkIds);

    void deleteByDocumentId(DocumentId documentId);
}
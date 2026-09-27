package com.example.rag.ingestion.domain.chunking;

import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;

import java.util.List;

public interface DocumentChunkRepository {

    void saveAll(List<DocumentChunk> chunks);

    List<DocumentChunk> findByDocumentId(DocumentId documentId);
}
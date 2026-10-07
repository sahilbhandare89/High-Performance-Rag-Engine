package com.example.rag.vector.application;

import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.document.DocumentId;

import java.util.Collection;
import java.util.List;

public interface VectorRepository {

    VectorRecord save(VectorRecord vector);

    void saveAll(Collection<VectorRecord> vectors);

    void deleteByDocumentId(DocumentId documentId);

    List<VectorSearchResult> search(VectorSearchRequest request);
}
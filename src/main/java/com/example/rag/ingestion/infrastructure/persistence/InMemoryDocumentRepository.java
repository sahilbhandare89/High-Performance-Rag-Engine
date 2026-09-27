package com.example.rag.ingestion.infrastructure.persistence;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.ingestion.domain.DocumentRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDocumentRepository
        implements DocumentRepository {

    private final Map<DocumentId, Document> documents =
            new ConcurrentHashMap<>();

    @Override
    public Document save(Document document) {
        documents.put(document.id(), document);
        return document;
    }
}
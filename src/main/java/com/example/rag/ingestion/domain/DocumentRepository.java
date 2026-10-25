package com.example.rag.ingestion.domain;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;

import java.util.Optional;

public interface DocumentRepository {

    Document save(Document document);

    Optional<Document> findById(DocumentId documentId);
}
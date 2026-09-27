package com.example.rag.ingestion.domain;

import com.example.rag.ingestion.domain.document.Document;

public interface DocumentRepository {

    Document save(Document document);
}
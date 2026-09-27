package com.example.rag.ingestion.application;

import com.example.rag.ingestion.domain.document.Document;

public interface DocumentIngestionService {

    Document ingest(IngestDocumentCommand command);
}
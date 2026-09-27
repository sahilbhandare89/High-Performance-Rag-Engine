package com.example.rag.ingestion.application;

import com.example.rag.ingestion.domain.DocumentMetadata;

public record IngestDocumentCommand(
        String name,
        String content,
        DocumentMetadata metadata
) {
}
package com.example.rag.ingestion.infrastructure.api;

public record ChunkDocumentRequest(
        String name,
        String content
) {
}
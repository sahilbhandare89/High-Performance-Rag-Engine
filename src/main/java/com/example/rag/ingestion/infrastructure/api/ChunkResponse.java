package com.example.rag.ingestion.infrastructure.api;

public record ChunkResponse(
        String id,
        String documentId,
        String content,
        int position
) {
}
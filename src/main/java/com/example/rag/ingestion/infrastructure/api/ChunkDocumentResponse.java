package com.example.rag.ingestion.infrastructure.api;

import java.util.List;

public record ChunkDocumentResponse(
        List<ChunkResponse> chunks
) {
}
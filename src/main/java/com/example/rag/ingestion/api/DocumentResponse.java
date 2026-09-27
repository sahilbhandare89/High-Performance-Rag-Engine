package com.example.rag.ingestion.api;

import com.example.rag.ingestion.domain.document.Document;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String name,
        String status,
        Instant createdAt
) {

    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.id().value(),
                document.name(),
                document.status().name(),
                document.createdAt()
        );
    }
}
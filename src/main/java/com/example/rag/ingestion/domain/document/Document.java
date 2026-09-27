package com.example.rag.ingestion.domain.document;

import com.example.rag.ingestion.domain.DocumentMetadata;
import com.example.rag.ingestion.domain.DocumentStatus;

import java.time.Instant;

public record Document(
        DocumentId id,
        String name,
        String content,
        DocumentMetadata metadata,
        DocumentStatus status,
        Instant createdAt
) {
    public Document {

        if (id == null) {
            throw new IllegalArgumentException("Document id cannot be null");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Document name cannot be empty");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Document content cannot be empty");
        }

        if (status == null) {
            throw new IllegalArgumentException("Document status cannot be null");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("Created time cannot be null");
        }
    }
}
package com.example.rag.ingestion.domain.document;

import java.util.UUID;

public record DocumentId(UUID value) {

    public static DocumentId generate() {
        return new DocumentId(UUID.randomUUID());
    }
}
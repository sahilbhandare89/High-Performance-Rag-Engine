package com.example.rag.ingestion.application.chunking;

import com.example.rag.ingestion.domain.document.Document;

import java.util.Objects;

public record ChunkDocumentCommand(
        Document document
) {

    public ChunkDocumentCommand {
        Objects.requireNonNull(
                document,
                "Document cannot be null"
        );
    }
}
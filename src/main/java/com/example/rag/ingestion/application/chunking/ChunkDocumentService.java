package com.example.rag.ingestion.application.chunking;

import com.example.rag.ingestion.domain.chunking.DocumentChunker;
import com.example.rag.ingestion.domain.document.DocumentChunk;

import java.util.List;
import java.util.Objects;

public class ChunkDocumentService {

    private final DocumentChunker documentChunker;

    public ChunkDocumentService(DocumentChunker documentChunker) {
        this.documentChunker = Objects.requireNonNull(
                documentChunker,
                "Document chunker cannot be null"
        );
    }

    public List<DocumentChunk> execute(
            ChunkDocumentCommand command
    ) {
        Objects.requireNonNull(
                command,
                "Chunk document command cannot be null"
        );

        return documentChunker.chunk(
                command.document()
        );
    }
}
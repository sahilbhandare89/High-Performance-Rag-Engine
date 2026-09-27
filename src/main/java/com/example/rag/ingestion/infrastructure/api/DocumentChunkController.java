package com.example.rag.ingestion.infrastructure.api;

import com.example.rag.ingestion.application.chunking.ChunkDocumentCommand;
import com.example.rag.ingestion.application.chunking.ChunkDocumentService;
import com.example.rag.ingestion.domain.DocumentMetadata;
import com.example.rag.ingestion.domain.DocumentStatus;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentChunkController {

    private final ChunkDocumentService chunkDocumentService;

    public DocumentChunkController(
            ChunkDocumentService chunkDocumentService
    ) {
        this.chunkDocumentService = chunkDocumentService;
    }

    @PostMapping("/chunks")
    public ChunkDocumentResponse chunk(
            @RequestBody ChunkDocumentRequest request
    ) {

        Document document = new Document(
                new DocumentId(UUID.randomUUID()),
                request.name(),
                request.content(),
                new DocumentMetadata(Map.of()),
                DocumentStatus.PROCESSING,
                Instant.now()
        );

        List<DocumentChunk> chunks =
                chunkDocumentService.execute(
                        new ChunkDocumentCommand(document)
                );

        return new ChunkDocumentResponse(
                chunks.stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private ChunkResponse toResponse(
            DocumentChunk chunk
    ) {
        return new ChunkResponse(
                chunk.id().value().toString(),
                chunk.documentId().value().toString(),
                chunk.content(),
                chunk.position()
        );
    }
}
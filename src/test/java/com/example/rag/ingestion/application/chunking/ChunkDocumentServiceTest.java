package com.example.rag.ingestion.application.chunking;

import com.example.rag.ingestion.domain.DocumentStatus;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.chunking.DocumentChunker;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.ingestion.domain.DocumentMetadata;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChunkDocumentServiceTest {

    @Test
    void shouldDelegateChunkingToDocumentChunker() {

        Document document = createDocument(
                "This is a test document."
        );

        DocumentChunk expectedChunk =
                new DocumentChunk(
                        ChunkId.generate(),
                        document.id(),
                        "This is a test document.",
                        0
                );

        DocumentChunker chunker =
                ignored -> List.of(expectedChunk);

        ChunkDocumentService service =
                new ChunkDocumentService(chunker);

        ChunkDocumentCommand command =
                new ChunkDocumentCommand(document);

        List<DocumentChunk> result =
                service.execute(command);

        assertEquals(
                List.of(expectedChunk),
                result
        );
    }

    private Document createDocument(String content) {

        return new Document(
                new DocumentId(UUID.randomUUID()),
                "test.txt",
                content,
                new DocumentMetadata(Map.of()),
                DocumentStatus.PROCESSING,
                Instant.now()
        );
    }

    @Test
    void shouldPassCommandDocumentToChunker() {

        Document document =
                createDocument("Original content.");

        Document[] captured =
                new Document[1];

        DocumentChunker chunker =
                input -> {
                    captured[0] = input;
                    return List.of();
                };

        ChunkDocumentService service =
                new ChunkDocumentService(chunker);

        service.execute(
                new ChunkDocumentCommand(document)
        );

        assertSame(
                document,
                captured[0]
        );
    }

    @Test
    void shouldReturnAllChunksFromChunker() {

        Document document =
                createDocument(
                        "A long document."
                );

        DocumentChunk first =
                new DocumentChunk(
                        ChunkId.generate(),
                        document.id(),
                        "A long",
                        0
                );

        DocumentChunk second =
                new DocumentChunk(
                        ChunkId.generate(),
                        document.id(),
                        "document.",
                        1
                );

        List<DocumentChunk> expected =
                List.of(first, second);

        DocumentChunker chunker =
                ignored -> expected;

        ChunkDocumentService service =
                new ChunkDocumentService(chunker);

        List<DocumentChunk> result =
                service.execute(
                        new ChunkDocumentCommand(document)
                );

        assertEquals(expected, result);
    }
}
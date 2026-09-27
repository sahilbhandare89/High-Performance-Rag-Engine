package com.example.rag.ingestion.domain;


import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentChunkTest {

    @Test
    void shouldCreateValidChunk() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        DocumentChunk chunk = new DocumentChunk(
                ChunkId.generate(),
                documentId,
                "Java virtual threads are lightweight.",
                0
        );

        assertNotNull(chunk.id());
        assertEquals(documentId, chunk.documentId());
        assertEquals(
                "Java virtual threads are lightweight.",
                chunk.content()
        );
        assertEquals(0, chunk.position());
    }

    @Test
    void shouldRejectNullContent() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        assertThrows(
                NullPointerException.class,
                () -> new DocumentChunk(
                        ChunkId.generate(),
                        documentId,
                        null,
                        0
                )
        );
    }

    @Test
    void shouldRejectEmptyContent() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        assertThrows(
                IllegalArgumentException.class,
                () -> new DocumentChunk(
                        ChunkId.generate(),
                        documentId,
                        "",
                        0
                )
        );
    }

    @Test
    void shouldRejectNegativePosition() {
        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        assertThrows(
                IllegalArgumentException.class,
                () -> new DocumentChunk(
                        ChunkId.generate(),
                        documentId,
                        "some content",
                        -1
                )
        );
    }

}
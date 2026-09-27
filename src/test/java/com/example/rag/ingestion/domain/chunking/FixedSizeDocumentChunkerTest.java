package com.example.rag.ingestion.domain.chunking;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentChunk;
import com.example.rag.ingestion.domain.document.DocumentId;
import org.junit.jupiter.api.Test;

import com.example.rag.ingestion.domain.DocumentMetadata;
import com.example.rag.ingestion.domain.DocumentStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FixedSizeDocumentChunkerTest {

    @Test
    void shouldSplitDocumentIntoFixedSizeChunks() {

        Document document = new Document(
                new DocumentId(UUID.randomUUID()),
                "test.txt",
                "abcdefghijklmnopqrst",
                new DocumentMetadata(Map.of()),
                DocumentStatus.PROCESSING,
                Instant.now()
        );

        FixedSizeDocumentChunker chunker =
                new FixedSizeDocumentChunker(5, 0);

        List<DocumentChunk> chunks = chunker.chunk(document);

        assertEquals(4, chunks.size());
    }

    @Test
    void shouldApplyOverlap() {

        Document document = new Document(
                new DocumentId(UUID.randomUUID()),
                "test.txt",
                "abcdefghijklmnopqrst",
                new DocumentMetadata(Map.of()),
                DocumentStatus.PROCESSING,
                Instant.now()
        );

        FixedSizeDocumentChunker chunker =
                new FixedSizeDocumentChunker(5, 2);

        var chunks = chunker.chunk(document);

        assertEquals(7, chunks.size());

        assertEquals("abcde", chunks.get(0).content());
        assertEquals("defgh", chunks.get(1).content());
        assertEquals("ghijk", chunks.get(2).content());
        assertEquals("jklmn", chunks.get(3).content());
        assertEquals("mnopq", chunks.get(4).content());
        assertEquals("pqrst", chunks.get(5).content());
        assertEquals("st", chunks.get(6).content());
    }

    @Test
    void shouldPreserveChunkPositions() {

        Document document = new Document(
                new DocumentId(UUID.randomUUID()),
                "test.txt",
                "abcdefghijkl",
                new DocumentMetadata(Map.of()),
                DocumentStatus.PROCESSING,
                Instant.now()
        );

        FixedSizeDocumentChunker chunker =
                new FixedSizeDocumentChunker(4, 0);

        var chunks = chunker.chunk(document);

        assertEquals(0, chunks.get(0).position());
        assertEquals(1, chunks.get(1).position());
        assertEquals(2, chunks.get(2).position());
    }

    @Test
    void shouldPreserveDocumentId() {

        DocumentId documentId =
                new DocumentId(UUID.randomUUID());

        Document document = new Document(
                documentId,
                "test.txt",
                "abcdefghijkl",
                new DocumentMetadata(Map.of()),
                DocumentStatus.PROCESSING,
                Instant.now()
        );

        FixedSizeDocumentChunker chunker =
                new FixedSizeDocumentChunker(4, 0);

        var chunks = chunker.chunk(document);

        assertEquals(
                documentId,
                chunks.get(0).documentId()
        );
    }

//    @Test
//    void shouldReturnEmptyListForEmptyDocument() {
//
//        Document document = new Document(
//                new DocumentId(UUID.randomUUID()),
//                "test.txt",
//                "",
//                new DocumentMetadata(Map.of()),
//                DocumentStatus.PROCESSING,
//                Instant.now()
//        );
//
//        FixedSizeDocumentChunker chunker =
//                new FixedSizeDocumentChunker(100, 10);
//
//        assertTrue(chunker.chunk(document).isEmpty());
//    }
//
    @Test
    void shouldRejectInvalidChunkSize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedSizeDocumentChunker(0, 0)
        );
    }

    @Test
    void shouldRejectNegativeOverlap() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedSizeDocumentChunker(100, -1)
        );
    }

    @Test
    void shouldRejectOverlapGreaterThanOrEqualToChunkSize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedSizeDocumentChunker(100, 100)
        );
    }
    @Test
    void shouldRejectZeroChunkSize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedSizeDocumentChunker(0, 0)
        );
    }

    @Test
    void shouldRejectNegativeChunkSize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedSizeDocumentChunker(-1, 0)
        );
    }
    @Test
    void shouldRejectOverlapEqualToChunkSize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedSizeDocumentChunker(10, 10)
        );
    }
    @Test
    void shouldRejectOverlapGreaterThanChunkSize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedSizeDocumentChunker(10, 11)
        );
    }



}
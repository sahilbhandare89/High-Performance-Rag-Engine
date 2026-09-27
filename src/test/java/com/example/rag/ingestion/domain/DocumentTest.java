package com.example.rag.ingestion.domain;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentTest {

    @Test
    void shouldCreateValidDocument() {

        Document document = new Document(
                DocumentId.generate(),
                "sample.txt",
                "This is the content.",
                new DocumentMetadata(Map.of()),
                DocumentStatus.RECEIVED,
                Instant.now()
        );

        assertNotNull(document);
        assertNotNull(document.id());
        assertEquals("sample.txt", document.name());
        assertEquals(
                "This is the content.",
                document.content()
        );
        assertEquals(
                DocumentStatus.RECEIVED,
                document.status()
        );
        assertNotNull(document.createdAt());
    }

    @Test
    void shouldRejectNullId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(
                        null,
                        "sample.txt",
                        "hello",
                        new DocumentMetadata(Map.of()),
                        DocumentStatus.RECEIVED,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectBlankName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(
                        DocumentId.generate(),
                        "",
                        "hello",
                        new DocumentMetadata(Map.of()),
                        DocumentStatus.RECEIVED,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectNullName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(
                        DocumentId.generate(),
                        null,
                        "hello",
                        new DocumentMetadata(Map.of()),
                        DocumentStatus.RECEIVED,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectBlankContent() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(
                        DocumentId.generate(),
                        "sample.txt",
                        "",
                        new DocumentMetadata(Map.of()),
                        DocumentStatus.RECEIVED,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectNullContent() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(
                        DocumentId.generate(),
                        "sample.txt",
                        null,
                        new DocumentMetadata(Map.of()),
                        DocumentStatus.RECEIVED,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectNullStatus() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(
                        DocumentId.generate(),
                        "sample.txt",
                        "hello",
                        new DocumentMetadata(Map.of()),
                        null,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldRejectNullCreatedAt() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(
                        DocumentId.generate(),
                        "sample.txt",
                        "hello",
                        new DocumentMetadata(Map.of()),
                        DocumentStatus.RECEIVED,
                        null
                )
        );
    }

    @Test
    void shouldAllowValidMetadata() {

        assertDoesNotThrow(
                () -> new Document(
                        DocumentId.generate(),
                        "sample.txt",
                        "hello",
                        new DocumentMetadata(
                                Map.of(
                                        "author", "John",
                                        "source", "upload"
                                )
                        ),
                        DocumentStatus.RECEIVED,
                        Instant.now()
                )
        );
    }
}
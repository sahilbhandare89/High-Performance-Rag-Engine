package com.example.rag.ingestion.application;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.DocumentMetadata;
import com.example.rag.ingestion.domain.DocumentRepository;
import com.example.rag.ingestion.domain.DocumentStatus;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultDocumentIngestionServiceTest {

    @Test
    void shouldIngestDocument() {

        FakeDocumentRepository repository =
                new FakeDocumentRepository();

        DefaultDocumentIngestionService service =
                new DefaultDocumentIngestionService(repository);

        IngestDocumentCommand command =
                new IngestDocumentCommand(
                        "sample.txt",
                        "This is the content.",
                        new DocumentMetadata(Map.of())
                );

        Document result = service.ingest(command);

        assertNotNull(result);

        assertNotNull(result.id());

        assertEquals(
                "sample.txt",
                result.name()
        );

        assertEquals(
                "This is the content.",
                result.content()
        );

        assertEquals(
                DocumentStatus.RECEIVED,
                result.status()
        );

        assertNotNull(result.createdAt());

        assertSame(
                result,
                repository.savedDocument
        );
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentDocuments() {

        FakeDocumentRepository repository =
                new FakeDocumentRepository();

        DefaultDocumentIngestionService service =
                new DefaultDocumentIngestionService(repository);

        IngestDocumentCommand command =
                new IngestDocumentCommand(
                        "sample.txt",
                        "hello",
                        new DocumentMetadata(Map.of())
                );

        Document first =
                service.ingest(command);

        Document second =
                service.ingest(command);

        assertNotEquals(
                first.id(),
                second.id()
        );
    }

    @Test
    void shouldSaveDocumentToRepository() {

        FakeDocumentRepository repository =
                new FakeDocumentRepository();

        DefaultDocumentIngestionService service =
                new DefaultDocumentIngestionService(repository);

        IngestDocumentCommand command =
                new IngestDocumentCommand(
                        "sample.txt",
                        "hello",
                        new DocumentMetadata(Map.of())
                );

        Document result =
                service.ingest(command);

        assertSame(
                result,
                repository.savedDocument
        );
    }

    private static class FakeDocumentRepository
            implements DocumentRepository {

        private Document savedDocument;

        @Override
        public Document save(Document document) {
            this.savedDocument = document;
            return document;
        }
    }
}
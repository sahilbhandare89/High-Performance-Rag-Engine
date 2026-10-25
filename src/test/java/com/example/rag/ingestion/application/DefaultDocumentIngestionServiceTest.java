
package com.example.rag.ingestion.application;

import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.DocumentMetadata;
import com.example.rag.ingestion.domain.DocumentRepository;
import com.example.rag.ingestion.domain.DocumentStatus;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDocumentIngestionServiceTest {

    @Test
    void shouldIngestDocument() {
        FakeDocumentRepository repository = new FakeDocumentRepository();
        FakeDocumentIndexer indexer = new FakeDocumentIndexer();

        DefaultDocumentIngestionService service =
                new DefaultDocumentIngestionService(repository, indexer);

        IngestDocumentCommand command = new IngestDocumentCommand(
                "sample.txt",
                "This is the content.",
                new DocumentMetadata(Map.of())
        );

        Document result = service.ingest(command);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("sample.txt", result.name());
        assertEquals("This is the content.", result.content());
        assertEquals(DocumentStatus.RECEIVED, result.status());
        assertNotNull(result.createdAt());

        assertSame(result, repository.savedDocument);
        assertSame(result, indexer.indexedDocument);
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentDocuments() {
        FakeDocumentRepository repository = new FakeDocumentRepository();
        FakeDocumentIndexer indexer = new FakeDocumentIndexer();

        DefaultDocumentIngestionService service =
                new DefaultDocumentIngestionService(repository, indexer);

        IngestDocumentCommand command = new IngestDocumentCommand(
                "sample.txt",
                "hello",
                new DocumentMetadata(Map.of())
        );

        Document first = service.ingest(command);
        Document second = service.ingest(command);

        assertNotEquals(first.id(), second.id());
    }

    @Test
    void shouldSaveDocumentToRepository() {
        FakeDocumentRepository repository = new FakeDocumentRepository();
        FakeDocumentIndexer indexer = new FakeDocumentIndexer();

        DefaultDocumentIngestionService service =
                new DefaultDocumentIngestionService(repository, indexer);

        IngestDocumentCommand command = new IngestDocumentCommand(
                "sample.txt",
                "hello",
                new DocumentMetadata(Map.of())
        );

        Document result = service.ingest(command);

        assertSame(result, repository.savedDocument);
    }

    @Test
    void shouldIndexDocumentAfterSavingIt() {
        FakeDocumentRepository repository = new FakeDocumentRepository();
        FakeDocumentIndexer indexer = new FakeDocumentIndexer();

        DefaultDocumentIngestionService service =
                new DefaultDocumentIngestionService(repository, indexer);

        IngestDocumentCommand command = new IngestDocumentCommand(
                "sample.txt",
                "hello",
                new DocumentMetadata(Map.of())
        );

        Document result = service.ingest(command);

        assertSame(result, repository.savedDocument);
        assertSame(result, indexer.indexedDocument);
    }

    private static class FakeDocumentRepository
            implements DocumentRepository {

        private Document savedDocument;

        @Override
        public Document save(Document document) {
            this.savedDocument = document;
            return document;
        }

        @Override
        public Optional<Document> findById(DocumentId documentId) {
            return Optional.empty();
        }
    }

    private static class FakeDocumentIndexer implements DocumentIndexer {

        private Document indexedDocument;

        @Override
        public List<VectorRecord> index(Document document) {
            this.indexedDocument = document;
            return List.of();
        }
    }
}


package com.example.rag.vector.infrastracture.memory;

import com.example.rag.ingestion.domain.DocumentRepository;
import com.example.rag.ingestion.domain.DocumentMetadata;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.vector.application.DocumentMetadataLookup;
import com.example.rag.vector.infrastracture.DocumentMetadataLookupAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentMetadataLookupAdapterTest {

    private InMemoryDocumentRepository documentRepository;
    private DocumentMetadataLookup metadataLookup;

    @BeforeEach
    void setUp() {
        documentRepository = new InMemoryDocumentRepository();
        metadataLookup =
                new DocumentMetadataLookupAdapter(documentRepository);
    }

    @Test
    void shouldMatchExistingMetadata() {
        DocumentId id = DocumentId.generate();

        documentRepository.save(createDocument(
                id,
                Map.of("department", "engineering")
        ));

        assertThat(metadataLookup.matches(
                id,
                Map.of("department", "engineering")
        )).isTrue();
    }

    @Test
    void shouldRejectDifferentMetadataValue() {
        DocumentId id = DocumentId.generate();

        documentRepository.save(createDocument(
                id,
                Map.of("department", "finance")
        ));

        assertThat(metadataLookup.matches(
                id,
                Map.of("department", "engineering")
        )).isFalse();
    }

    @Test
    void shouldRequireAllFiltersToMatch() {
        DocumentId id = DocumentId.generate();

        documentRepository.save(createDocument(
                id,
                Map.of(
                        "department", "engineering",
                        "type", "policy"
                )
        ));

        assertThat(metadataLookup.matches(
                id,
                Map.of(
                        "department", "engineering",
                        "type", "policy"
                )
        )).isTrue();

        assertThat(metadataLookup.matches(
                id,
                Map.of(
                        "department", "engineering",
                        "type", "report"
                )
        )).isFalse();
    }

    @Test
    void shouldMatchAnyDocumentWhenFiltersAreEmpty() {
        assertThat(metadataLookup.matches(
                DocumentId.generate(),
                Map.of()
        )).isTrue();
    }

    @Test
    void shouldRejectMissingDocumentWhenFiltersArePresent() {
        assertThat(metadataLookup.matches(
                DocumentId.generate(),
                Map.of("department", "engineering")
        )).isFalse();
    }

    private Document createDocument(
            DocumentId id,
            Map<String, String> metadata
    ) {
        return new Document(
                id,
                "test.txt",
                "Test document content",
                new DocumentMetadata(metadata),
                com.example.rag.ingestion.domain.DocumentStatus.RECEIVED,
                Instant.now()
        );
    }

    private static class InMemoryDocumentRepository
            implements DocumentRepository {

        private final Map<DocumentId, Document> documents =
                new java.util.concurrent.ConcurrentHashMap<>();

        @Override
        public Document save(Document document) {
            documents.put(document.id(), document);
            return document;
        }

        @Override
        public Optional<Document> findById(DocumentId documentId) {
            return Optional.ofNullable(documents.get(documentId));
        }
    }
}


package com.example.rag.ingestion.application;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.ingestion.domain.DocumentRepository;
import com.example.rag.ingestion.domain.DocumentStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class DefaultDocumentIngestionService
        implements DocumentIngestionService {

    private final DocumentRepository documentRepository;
    private final DocumentIndexer documentIndexer;

    public DefaultDocumentIngestionService(
            DocumentRepository documentRepository,
            DocumentIndexer documentIndexer
    ) {
        this.documentRepository = Objects.requireNonNull(
                documentRepository,
                "documentRepository must not be null"
        );

        this.documentIndexer = Objects.requireNonNull(
                documentIndexer,
                "documentIndexer must not be null"
        );
    }

    @Override
    public Document ingest(IngestDocumentCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Document document = new Document(
                DocumentId.generate(),
                command.name(),
                command.content(),
                command.metadata(),
                DocumentStatus.RECEIVED,
                Instant.now()
        );

        Document savedDocument = documentRepository.save(document);

        documentIndexer.index(savedDocument);

        return savedDocument;
    }
}

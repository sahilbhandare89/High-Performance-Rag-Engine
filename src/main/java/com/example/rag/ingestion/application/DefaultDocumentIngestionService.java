package com.example.rag.ingestion.application;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.ingestion.domain.DocumentRepository;
import com.example.rag.ingestion.domain.DocumentStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DefaultDocumentIngestionService
        implements DocumentIngestionService {

    private final DocumentRepository documentRepository;

    public DefaultDocumentIngestionService(
            DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Document ingest(IngestDocumentCommand command) {

        DocumentId documentId = DocumentId.generate();

        Document document = new Document(
                documentId,
                command.name(),
                command.content(),
                command.metadata(),
                DocumentStatus.RECEIVED,
                Instant.now()
        );

        return documentRepository.save(document);
    }
}
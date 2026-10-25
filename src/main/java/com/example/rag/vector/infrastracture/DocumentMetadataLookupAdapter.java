
package com.example.rag.vector.infrastracture;

import com.example.rag.ingestion.domain.DocumentRepository;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.vector.application.DocumentMetadataLookup;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
public class DocumentMetadataLookupAdapter
        implements DocumentMetadataLookup {

    private final DocumentRepository documentRepository;

    public DocumentMetadataLookupAdapter(
            DocumentRepository documentRepository
    ) {
        this.documentRepository = Objects.requireNonNull(
                documentRepository,
                "documentRepository must not be null"
        );
    }

    @Override
    public boolean matches(
            DocumentId documentId,
            Map<String, String> filters
    ) {
        Objects.requireNonNull(documentId, "documentId must not be null");
        Objects.requireNonNull(filters, "filters must not be null");

        if (filters.isEmpty()) {
            return true;
        }

        return documentRepository.findById(documentId)
                .map(document -> filters.entrySet().stream()
                        .allMatch(filter -> Objects.equals(
                                document.metadata().values()
                                        .get(filter.getKey()),
                                filter.getValue()
                        )))
                .orElse(false);
    }
}


package com.example.rag.vector.application;

import com.example.rag.ingestion.domain.document.DocumentId;

import java.util.Map;

public interface DocumentMetadataLookup {

    boolean matches(
            DocumentId documentId,
            Map<String, String> filters
    );
}

package com.example.rag.ingestion.api;

import com.example.rag.ingestion.application.DocumentIngestionService;
import com.example.rag.ingestion.application.IngestDocumentCommand;
import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.DocumentMetadata;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentIngestionService ingestionService;

    public DocumentController(
            DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> ingest(
            @Valid @RequestBody CreateDocumentRequest request) {

        IngestDocumentCommand command =
                new IngestDocumentCommand(
                        request.name(),
                        request.content(),
                        new DocumentMetadata(request.metadata())
                );

        Document document = ingestionService.ingest(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DocumentResponse.from(document));
    }
}
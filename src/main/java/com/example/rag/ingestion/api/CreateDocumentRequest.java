package com.example.rag.ingestion.api;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record CreateDocumentRequest(
        @NotBlank(message = "Document name must not be blank")
        String name,

        @NotBlank(message = "Document content must not be blank")
        String content,

        Map<String, String> metadata
) {
}
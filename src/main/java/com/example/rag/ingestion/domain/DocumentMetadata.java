package com.example.rag.ingestion.domain;

import java.util.Map;

public record DocumentMetadata(
        Map<String, String> values
) {
}
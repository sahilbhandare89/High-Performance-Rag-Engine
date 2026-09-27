package com.example.rag.ingestion.api;

public record ApiErrorResponse(
        String code,
        String message
) {
}
package com.example.rag.embedding.application.exception;

public class NonRetryableEmbeddingException
        extends EmbeddingException {

    public NonRetryableEmbeddingException(String message) {
        super(message);
    }

    public NonRetryableEmbeddingException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
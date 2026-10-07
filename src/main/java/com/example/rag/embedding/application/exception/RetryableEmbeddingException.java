package com.example.rag.embedding.application.exception;

public class RetryableEmbeddingException
        extends EmbeddingException {

    public RetryableEmbeddingException(String message) {
        super(message);
    }

    public RetryableEmbeddingException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
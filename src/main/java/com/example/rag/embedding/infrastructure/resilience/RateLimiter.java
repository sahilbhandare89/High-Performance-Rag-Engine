package com.example.rag.embedding.infrastructure.resilience;

@FunctionalInterface
public interface RateLimiter {

    void acquire();
}

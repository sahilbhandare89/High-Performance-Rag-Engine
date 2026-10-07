package com.example.rag.embedding.infrastructure.resilience;

import java.time.Duration;

@FunctionalInterface
public interface BackoffStrategy {

    Duration calculate(int attempt);
}
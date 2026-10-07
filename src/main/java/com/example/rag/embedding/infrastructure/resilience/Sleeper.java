package com.example.rag.embedding.infrastructure.resilience;

import java.time.Duration;

@FunctionalInterface
public interface Sleeper {

    void sleep(Duration duration);
}
package com.example.rag.embedding.infrastructure.resilience;

@FunctionalInterface
public interface JitterRandom {

    long nextLong(long bound);
}
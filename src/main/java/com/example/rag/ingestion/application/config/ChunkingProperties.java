package com.example.rag.ingestion.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rag.chunking.fixed-size")
public record ChunkingProperties(
        int chunkSize,
        int overlap
) {
}
package com.example.rag.ingestion.infrastructure.config;

import com.example.rag.ingestion.application.chunking.ChunkDocumentService;
import com.example.rag.ingestion.application.config.ChunkingProperties;
import com.example.rag.ingestion.domain.chunking.DocumentChunker;
import com.example.rag.ingestion.domain.chunking.FixedSizeDocumentChunker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChunkingConfiguration {

    @Bean
    public DocumentChunker documentChunker(
            ChunkingProperties properties
    ) {
        return new FixedSizeDocumentChunker(
                properties.chunkSize(),
                properties.overlap()
        );
    }

    @Bean
    public ChunkDocumentService chunkDocumentService(
            DocumentChunker documentChunker
    ) {
        return new ChunkDocumentService(documentChunker);
    }
}
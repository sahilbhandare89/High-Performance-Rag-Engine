package com.example.rag.embedding.infrastructure.config;

import com.example.rag.embedding.application.DefaultEmbeddingService;
import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.infrastructure.provider.EmbeddingProviderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfiguration {

    @Bean
    EmbeddingProvider embeddingProvider(
            EmbeddingProperties properties
    ) {
        return EmbeddingProviderFactory.create(properties);
    }

    @Bean
    EmbeddingService embeddingService(
            EmbeddingProvider provider,
            EmbeddingProperties properties
    ) {
        return new DefaultEmbeddingService(
                provider,
                properties.getBatchSize()
        );
    }
}
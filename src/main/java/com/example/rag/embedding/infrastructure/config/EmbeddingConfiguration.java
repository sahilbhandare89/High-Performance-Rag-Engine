package com.example.rag.embedding.infrastructure.config;

import com.example.rag.embedding.application.DefaultEmbeddingService;
import com.example.rag.embedding.application.EmbeddingService;
import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.infrastructure.provider.EmbeddingProviderFactory;
import com.example.rag.embedding.infrastructure.resilience.BackoffStrategy;
import com.example.rag.embedding.infrastructure.resilience.ExponentialBackoffWithJitter;
import com.example.rag.embedding.infrastructure.resilience.FixedRateLimiter;
import com.example.rag.embedding.infrastructure.resilience.RateLimiter;
import com.example.rag.embedding.infrastructure.resilience.ResilientEmbeddingProvider;
import com.example.rag.embedding.infrastructure.resilience.RetryExecutor;
import com.example.rag.embedding.infrastructure.resilience.RetryPolicy;
import com.example.rag.embedding.infrastructure.resilience.Sleeper;
import com.example.rag.embedding.infrastructure.resilience.ThreadSleeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingConfiguration {

    @Bean
    RetryPolicy retryPolicy(
            EmbeddingProperties properties
    ) {

        EmbeddingProperties.RetryProperties retry =
                properties.getRetry();

        return new RetryPolicy(
                retry.getMaxAttempts(),
                retry.getInitialBackoff(),
                retry.getMaxBackoff()
        );
    }

    @Bean
    BackoffStrategy backoffStrategy(
            EmbeddingProperties properties
    ) {

        EmbeddingProperties.RetryProperties retry =
                properties.getRetry();

        return new ExponentialBackoffWithJitter(
                retry.getInitialBackoff(),
                retry.getMaxBackoff()
        );
    }

    @Bean
    Sleeper sleeper() {
        return new ThreadSleeper();
    }

    @Bean
    RetryExecutor retryExecutor(
            RetryPolicy retryPolicy,
            BackoffStrategy backoffStrategy,
            Sleeper sleeper
    ) {
        return new RetryExecutor(
                retryPolicy,
                backoffStrategy,
                sleeper
        );
    }

    @Bean
    RateLimiter rateLimiter(
            EmbeddingProperties properties
    ) {

        return new FixedRateLimiter(
                properties
                        .getRateLimit()
                        .getInterval()
        );
    }

    @Bean
    EmbeddingProvider embeddingProvider(
            EmbeddingProperties properties,
            RetryExecutor retryExecutor,
            RateLimiter rateLimiter
    ) {

        EmbeddingProvider rawProvider =
                EmbeddingProviderFactory.create(
                        properties
                );

        return new ResilientEmbeddingProvider(
                rawProvider,
                retryExecutor,
                rateLimiter
        );
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
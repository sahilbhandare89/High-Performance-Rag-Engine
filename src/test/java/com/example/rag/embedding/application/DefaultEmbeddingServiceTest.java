
package com.example.rag.embedding.application;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultEmbeddingServiceTest {

    @Mock
    private EmbeddingProvider provider;

    @Mock
    private Embedding embedding;

    private DefaultEmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        embeddingService = new DefaultEmbeddingService(
                provider,
                10
        );
    }

    @Test
    void shouldEmbedRawText() {
        String text = "How does semantic search work?";

        when(provider.embed(text)).thenReturn(embedding);

        Embedding result = embeddingService.embedQuery(text);

        assertSame(embedding, result);

        verify(provider, times(1)).embed(text);
    }

    @Test
    void shouldRejectNullText() {
        assertThrows(
                NullPointerException.class,
                () -> embeddingService.embedQuery((String) null)
        );

        verifyNoInteractions(provider);
    }

    @Test
    void shouldRejectBlankText() {
        assertThrows(
                IllegalArgumentException.class,
                () -> embeddingService.embedQuery("   ")
        );

        verifyNoInteractions(provider);
    }
}

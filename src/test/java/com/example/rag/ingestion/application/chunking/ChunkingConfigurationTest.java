package com.example.rag.ingestion.application.chunking;

import com.example.rag.ingestion.application.chunking.ChunkDocumentService;
import com.example.rag.ingestion.domain.chunking.DocumentChunker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ChunkingConfigurationTest {

    @Autowired
    private DocumentChunker documentChunker;

    @Autowired
    private ChunkDocumentService chunkDocumentService;

    @Test
    void shouldCreateChunkingBeans() {
        assertNotNull(documentChunker);
        assertNotNull(chunkDocumentService);
    }
}
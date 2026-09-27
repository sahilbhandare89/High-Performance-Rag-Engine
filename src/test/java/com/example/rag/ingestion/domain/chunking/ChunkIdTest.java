package com.example.rag.ingestion.domain.chunking;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChunkIdTest {

    @Test
    void shouldGenerateChunkId() {

        ChunkId chunkId = ChunkId.generate();

        assertNotNull(chunkId);
        assertNotNull(chunkId.value());
    }

    @Test
    void shouldRejectNullValue() {

        assertThrows(
                NullPointerException.class,
                () -> new ChunkId(null)
        );
    }

    @Test
    void shouldPreserveUuid() {

        UUID uuid = UUID.randomUUID();

        ChunkId chunkId = new ChunkId(uuid);

        assertEquals(uuid, chunkId.value());
    }

    @Test
    void shouldGenerateDifferentIds() {

        ChunkId first = ChunkId.generate();
        ChunkId second = ChunkId.generate();

        assertNotEquals(first, second);
    }
}
package com.example.rag.ingestion.domain.chunking;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FixedSizeDocumentChunker implements DocumentChunker {

    private final int chunkSize;
    private final int overlap;

    public FixedSizeDocumentChunker(int chunkSize, int overlap) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException(
                    "Chunk size must be greater than zero"
            );
        }

        if (overlap < 0) {
            throw new IllegalArgumentException(
                    "Chunk overlap cannot be negative"
            );
        }

        if (overlap >= chunkSize) {
            throw new IllegalArgumentException(
                    "Chunk overlap must be smaller than chunk size"
            );
        }

        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }

    @Override
    public List<DocumentChunk> chunk(Document document) {

        Objects.requireNonNull(
                document,
                "Document cannot be null"
        );

        String content = document.content();

        if (content == null || content.isEmpty()) {
            return List.of();
        }

        List<DocumentChunk> chunks = new ArrayList<>();

        int step = chunkSize - overlap;
        int position = 0;

        for (int start = 0; start < content.length(); start += step) {

            int end = Math.min(
                    start + chunkSize,
                    content.length()
            );

            String chunkContent = content.substring(start, end);

            chunks.add(
                    new DocumentChunk(
                            ChunkId.generate(),
                            document.id(),
                            chunkContent,
                            position++
                    )
            );
        }

        return List.copyOf(chunks);
    }
}


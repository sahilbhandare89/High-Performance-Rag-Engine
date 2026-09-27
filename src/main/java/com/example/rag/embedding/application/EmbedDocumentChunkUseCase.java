package com.example.rag.embedding.application;


import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.document.DocumentChunk;

public interface EmbedDocumentChunkUseCase {

    VectorRecord execute(DocumentChunk chunk);
}
package com.example.rag.ingestion.domain.chunking;

import com.example.rag.ingestion.domain.document.Document;
import com.example.rag.ingestion.domain.document.DocumentChunk;

import java.util.List;

public interface DocumentChunker {

    List<DocumentChunk> chunk(Document document);
}
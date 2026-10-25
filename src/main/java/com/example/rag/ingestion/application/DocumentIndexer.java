package com.example.rag.ingestion.application;

import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.document.Document;

import java.util.List;

public interface DocumentIndexer {
    List<VectorRecord> index(Document document);
}
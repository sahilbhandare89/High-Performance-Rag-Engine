package com.example.rag.vector.infrastracture.memory;

import com.example.rag.embedding.domain.VectorRecord;
import com.example.rag.ingestion.domain.chunking.ChunkId;
import com.example.rag.ingestion.domain.document.DocumentId;
import com.example.rag.vector.application.VectorRepository;
import com.example.rag.vector.application.VectorSearchRequest;
import com.example.rag.vector.application.VectorSearchResult;
import com.example.rag.vector.application.similarity.SimilarityCalculator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVectorRepository implements VectorRepository {

    private final Map<VectorKey, VectorRecord> vectors =
            new ConcurrentHashMap<>();

    private final SimilarityCalculator similarityCalculator;

    public InMemoryVectorRepository(
            SimilarityCalculator similarityCalculator
    ) {
        this.similarityCalculator = Objects.requireNonNull(
                similarityCalculator,
                "similarityCalculator must not be null"
        );
    }

    @Override
    public VectorRecord save(VectorRecord vector) {

        Objects.requireNonNull(
                vector,
                "vector must not be null"
        );

        VectorKey key = new VectorKey(
                vector.documentId(),
                vector.chunkId()
        );

        vectors.put(key, vector);

        return vector;
    }

    @Override
    public void saveAll(Collection<VectorRecord> vectors) {

        Objects.requireNonNull(
                vectors,
                "vectors must not be null"
        );

        for (VectorRecord vector : vectors) {
            save(vector);
        }
    }

    @Override
    public void deleteByDocumentId(DocumentId documentId) {

        Objects.requireNonNull(
                documentId,
                "documentId must not be null"
        );

        vectors.keySet().removeIf(
                key -> key.documentId().equals(documentId)
        );
    }

    @Override
    public List<VectorSearchResult> search(
            VectorSearchRequest request
    ) {
        Objects.requireNonNull(
                request,
                "request must not be null"
        );

        PriorityQueue<VectorSearchResult> topResults =
                new PriorityQueue<>(
                        request.topK(),
                        (first, second) ->
                                Double.compare(
                                        first.similarity(),
                                        second.similarity()
                                )
                );

        for (VectorRecord vector : vectors.values()) {

            double similarity =
                    similarityCalculator.cosineSimilarity(
                            request.queryEmbedding(),
                            vector.embedding()
                    );

            VectorSearchResult result =
                    new VectorSearchResult(
                            vector,
                            similarity
                    );

            if (topResults.size() < request.topK()) {

                topResults.offer(result);

            } else if (
                    similarity >
                            topResults.peek().similarity()
            ) {

                topResults.poll();
                topResults.offer(result);
            }
        }

        return topResults.stream()
                .sorted(
                        (first, second) ->
                                Double.compare(
                                        second.similarity(),
                                        first.similarity()
                                )
                )
                .toList();
    }

    private record VectorKey(
            DocumentId documentId,
            ChunkId chunkId
    ) {
    }
}
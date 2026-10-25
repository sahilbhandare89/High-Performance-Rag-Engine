//package com.example.rag.vector.infrastructure.pgvector;
//
//import com.example.rag.embedding.domain.Embedding;
//import com.example.rag.embedding.domain.VectorRecord;
//import com.example.rag.ingestion.domain.chunking.ChunkId;
//import com.example.rag.ingestion.domain.document.DocumentId;
//import com.example.rag.vector.application.VectorRepository;
//import com.example.rag.vector.application.VectorSearchRequest;
//import com.example.rag.vector.application.VectorSearchResult;
//import org.springframework.jdbc.core.simple.JdbcClient;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//
//
//public class PgVectorRepository implements VectorRepository {
//
//    private static final String UPSERT = """
//            INSERT INTO vector_records (document_id, chunk_id, embedding)
//            VALUES (:documentId, :chunkId, CAST(:embedding AS vector))
//            ON CONFLICT (document_id, chunk_id)
//            DO UPDATE SET embedding = EXCLUDED.embedding
//            """;
//
//    private static final String SEARCH = """
//            SELECT document_id,
//                   chunk_id,
//                   embedding::text AS embedding,
//                   1 - (embedding <=> CAST(:query AS vector)) AS similarity
//            FROM vector_records
//            ORDER BY embedding <=> CAST(:query AS vector)
//            LIMIT :topK
//            """;
//
//    private final JdbcClient jdbc;
//
//    public PgVectorRepository(JdbcClient jdbc) {
//        this.jdbc = Objects.requireNonNull(jdbc);
//    }
//
//    @Override
//    public VectorRecord save(VectorRecord vector) {
//        Objects.requireNonNull(vector, "vector must not be null");
//
//        jdbc.sql(UPSERT)
//                .param("documentId", vector.documentId().value())
//                .param("chunkId", vector.chunkId().value())
//                .param("embedding", toLiteral(vector.embedding()))
//                .update();
//
//        return vector;
//    }
//
//    @Override
//    @Transactional
//    public void saveAll(Collection<VectorRecord> vectors) {
//        Objects.requireNonNull(vectors, "vectors must not be null");
//        vectors.forEach(this::save);
//    }
//
//    @Override
//    public void deleteByDocumentId(DocumentId documentId) {
//        Objects.requireNonNull(documentId, "documentId must not be null");
//
//        jdbc.sql("DELETE FROM vector_records WHERE document_id = :id")
//                .param("id", documentId.value())
//                .update();
//    }
//
//    @Override
//    public List<VectorSearchResult> search(VectorSearchRequest request) {
//        Objects.requireNonNull(request, "request must not be null");
//
//        return jdbc.sql(SEARCH)
//                .param("query", toLiteral(request.queryEmbedding()))
//                .param("topK", request.topK())
//                .query((rs, rowNum) -> new VectorSearchResult(
//                        new VectorRecord(
//                                new DocumentId(rs.getObject("document_id", UUID.class)),
//                                new ChunkId(rs.getObject("chunk_id", UUID.class)),
//                                parse(rs.getString("embedding"))
//                        ),
//                        rs.getDouble("similarity")
//                ))
//                .list();
//    }
//
//    private static String toLiteral(Embedding embedding) {
//        float[] v = embedding.values();
//        StringBuilder sb = new StringBuilder("[");
//        for (int i = 0; i < v.length; i++) {
//            if (i > 0) sb.append(',');
//            sb.append(v[i]);
//        }
//        return sb.append(']').toString();
//    }
//
//    private static Embedding parse(String literal) {
//        String[] parts = literal.substring(1, literal.length() - 1).split(",");
//        float[] values = new float[parts.length];
//        for (int i = 0; i < parts.length; i++) {
//            values[i] = Float.parseFloat(parts[i]);
//        }
//        return new Embedding(values);
//    }
//}
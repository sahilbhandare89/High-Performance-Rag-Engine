//package com.example.rag.ingestion.infrastructure.persistence;
//
//import com.example.rag.ingestion.domain.chunking.ChunkId;
//import com.example.rag.ingestion.domain.chunking.DocumentChunkRepository;
//import com.example.rag.ingestion.domain.document.DocumentChunk;
//import com.example.rag.ingestion.domain.document.DocumentId;
//import org.springframework.jdbc.core.simple.JdbcClient;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.UUID;
//
//
//public class PostgresDocumentChunkRepository implements DocumentChunkRepository {
//
//    private static final String UPSERT = """
//            INSERT INTO document_chunks (chunk_id, document_id, content, position)
//            VALUES (:id, :docId, :content, :position)
//            ON CONFLICT (chunk_id)
//            DO UPDATE SET content = EXCLUDED.content, position = EXCLUDED.position
//            """;
//
//    private final JdbcClient jdbc;
//
//    public PostgresDocumentChunkRepository(JdbcClient jdbc) {
//        this.jdbc = Objects.requireNonNull(jdbc);
//    }
//
//    @Override
//    @Transactional
//    public void saveAll(List<DocumentChunk> chunks) {
//        Objects.requireNonNull(chunks, "chunks must not be null");
//
//        for (DocumentChunk c : chunks) {
//            jdbc.sql(UPSERT)
//                    .param("id", c.id().value())
//                    .param("docId", c.documentId().value())
//                    .param("content", c.content())
//                    .param("position", c.position())
//                    .update();
//        }
//    }
//
//    @Override
//    public List<DocumentChunk> findByDocumentId(DocumentId documentId) {
//        Objects.requireNonNull(documentId, "documentId must not be null");
//
//        return jdbc.sql("""
//                        SELECT chunk_id, document_id, content, position
//                        FROM document_chunks
//                        WHERE document_id = :id
//                        ORDER BY position
//                        """)
//                .param("id", documentId.value())
//                .query((rs, n) -> map(rs))
//                .list();
//    }
//
//    @Override
//    public Map<ChunkId, DocumentChunk> findAllById(Collection<ChunkId> chunkIds) {
//        Objects.requireNonNull(chunkIds, "chunkIds must not be null");
//
//        if (chunkIds.isEmpty()) {
//            return Map.of();   // "IN ()" is invalid SQL
//        }
//
//        List<UUID> ids = chunkIds.stream().map(ChunkId::value).toList();
//
//        List<DocumentChunk> found = jdbc.sql("""
//                        SELECT chunk_id, document_id, content, position
//                        FROM document_chunks
//                        WHERE chunk_id IN (:ids)
//                        """)
//                .param("ids", ids)
//                .query((rs, n) -> map(rs))
//                .list();
//
//        Map<ChunkId, DocumentChunk> result = new LinkedHashMap<>();
//        for (DocumentChunk chunk : found) {
//            result.put(chunk.id(), chunk);
//        }
//        return result;
//    }
//
//    @Override
//    @Transactional
//    public void deleteByDocumentId(DocumentId documentId) {
//        Objects.requireNonNull(documentId, "documentId must not be null");
//
//        jdbc.sql("DELETE FROM document_chunks WHERE document_id = :id")
//                .param("id", documentId.value())
//                .update();
//    }
//
//    private static DocumentChunk map(java.sql.ResultSet rs) throws java.sql.SQLException {
//        return new DocumentChunk(
//                new ChunkId(rs.getObject("chunk_id", UUID.class)),
//                new DocumentId(rs.getObject("document_id", UUID.class)),
//                rs.getString("content"),
//                rs.getInt("position"));
//    }
//}
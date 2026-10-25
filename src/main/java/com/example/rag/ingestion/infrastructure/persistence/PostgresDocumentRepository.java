//
//package com.example.rag.ingestion.infrastructure.persistence;
//
//import com.example.rag.ingestion.domain.DocumentMetadata;
//import com.example.rag.ingestion.domain.DocumentRepository;
//import com.example.rag.ingestion.domain.DocumentStatus;
//import com.example.rag.ingestion.domain.document.Document;
//import com.example.rag.ingestion.domain.document.DocumentId;
//
//import org.springframework.jdbc.core.simple.JdbcClient;
//import org.springframework.stereotype.Repository;
//
//import tools.jackson.core.JacksonException;
//import tools.jackson.core.type.TypeReference;
//import tools.jackson.databind.ObjectMapper;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.UUID;
//
//
//public class PostgresDocumentRepository implements DocumentRepository {
//
//    private static final TypeReference<Map<String, String>> MAP_TYPE =
//            new TypeReference<>() {};
//
//    private static final String UPSERT = """
//            INSERT INTO documents (
//                id, name, content, metadata, status, created_at
//            )
//            VALUES (
//                :id, :name, :content, CAST(:metadata AS jsonb),
//                :status, :createdAt
//            )
//            ON CONFLICT (id) DO UPDATE SET
//                name = EXCLUDED.name,
//                content = EXCLUDED.content,
//                metadata = EXCLUDED.metadata,
//                status = EXCLUDED.status
//            """;
//
//    private final JdbcClient jdbc;
//    private final ObjectMapper mapper;
//
//    public PostgresDocumentRepository(
//            JdbcClient jdbc,
//            ObjectMapper mapper) {
//        this.jdbc = Objects.requireNonNull(jdbc);
//        this.mapper = Objects.requireNonNull(mapper);
//    }
//
//    @Override
//    public Document save(Document document) {
//        Objects.requireNonNull(document, "document must not be null");
//
//        jdbc.sql(UPSERT)
//                .param("id", document.id().value())
//                .param("name", document.name())
//                .param("content", document.content())
//                .param("metadata", toJson(document.metadata()))
//                .param("status", document.status().name())
//                .param(
//                        "createdAt",
//                        Timestamp.from(document.createdAt()))
//                .update();
//
//        return document;
//    }
//
//    @Override
//    public Optional<Document> findById(DocumentId documentId) {
//        Objects.requireNonNull(
//                documentId, "documentId must not be null");
//
//        return jdbc.sql("""
//                        SELECT id, name, content,
//                               metadata::text AS metadata,
//                               status, created_at
//                        FROM documents
//                        WHERE id = :id
//                        """)
//                .param("id", documentId.value())
//                .query((rs, rowNum) -> map(rs))
//                .optional();
//    }
//
//    private Document map(ResultSet rs) throws SQLException {
//        Timestamp createdAt = rs.getTimestamp("created_at");
//
//        if (createdAt == null) {
//            throw new IllegalStateException(
//                    "Document created_at must not be null");
//        }
//
//        return new Document(
//                new DocumentId(rs.getObject("id", UUID.class)),
//                rs.getString("name"),
//                rs.getString("content"),
//                fromJson(rs.getString("metadata")),
//                DocumentStatus.valueOf(rs.getString("status")),
//                createdAt.toInstant()
//        );
//    }
//
//    private String toJson(DocumentMetadata metadata) {
//        Map<String, String> values =
//                metadata == null || metadata.values() == null
//                        ? Map.of()
//                        : metadata.values();
//
//        try {
//            return mapper.writeValueAsString(values);
//        } catch (JacksonException e) {
//            throw new IllegalStateException(
//                    "Cannot serialize document metadata", e);
//        }
//    }
//
//    private DocumentMetadata fromJson(String json) {
//        if (json == null || json.isBlank()) {
//            return new DocumentMetadata(Map.of());
//        }
//
//        try {
//            Map<String, String> values =
//                    mapper.readValue(json, MAP_TYPE);
//
//            return new DocumentMetadata(
//                    values == null ? Map.of() : values);
//        } catch (JacksonException e) {
//            throw new IllegalStateException(
//                    "Cannot parse document metadata", e);
//        }
//    }
//}

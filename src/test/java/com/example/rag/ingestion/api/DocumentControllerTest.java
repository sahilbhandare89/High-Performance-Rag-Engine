//package com.example.rag.ingestion.api;
//
//import com.example.rag.ingestion.application.DocumentIngestionService;
//import com.example.rag.ingestion.application.IngestDocumentCommand;
//import com.example.rag.ingestion.domain.document.Document;
//import com.example.rag.ingestion.domain.document.DocumentId;
//import com.example.rag.ingestion.domain.DocumentMetadata;
//import com.example.rag.ingestion.domain.DocumentStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Instant;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoInteractions;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(DocumentController.class)
//class DocumentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private DocumentIngestionService ingestionService;
//
//    @Test
//    void shouldCreateDocument() throws Exception {
//
//        Document document = new Document(
//                DocumentId.generate(),
//                "sample.txt",
//                "hello",
//                new DocumentMetadata(Map.of()),
//                DocumentStatus.RECEIVED,
//                Instant.now()
//        );
//
//        when(ingestionService.ingest(any(IngestDocumentCommand.class)))
//                .thenReturn(document);
//
//        mockMvc.perform(
//                        post("/api/v1/documents")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("""
//                                {
//                                  "name": "sample.txt",
//                                  "content": "hello"
//                                }
//                                """)
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").exists())
//                .andExpect(jsonPath("$.name").value("sample.txt"))
//                .andExpect(jsonPath("$.status").value("RECEIVED"))
//                .andExpect(jsonPath("$.createdAt").exists());
//
//        verify(ingestionService)
//                .ingest(any(IngestDocumentCommand.class));
//    }
//
//    @Test
//    void shouldRejectBlankContent() throws Exception {
//
//        mockMvc.perform(
//                        post("/api/v1/documents")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("""
//                                {
//                                  "name": "sample.txt",
//                                  "content": ""
//                                }
//                                """)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(
//                        jsonPath("$.code")
//                                .value("DOCUMENT_VALIDATION_FAILED")
//                )
//                .andExpect(
//                        jsonPath("$.message")
//                                .value("Document content must not be blank")
//                );
//
//        verifyNoInteractions(ingestionService);
//    }
//
//    @Test
//    void shouldRejectBlankName() throws Exception {
//
//        mockMvc.perform(
//                        post("/api/v1/documents")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("""
//                                {
//                                  "name": "",
//                                  "content": "hello"
//                                }
//                                """)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(
//                        jsonPath("$.code")
//                                .value("DOCUMENT_VALIDATION_FAILED")
//                )
//                .andExpect(
//                        jsonPath("$.message")
//                                .value("Document name must not be blank")
//                );
//
//        verifyNoInteractions(ingestionService);
//    }
//
//    @Test
//    void shouldRejectMissingFields() throws Exception {
//
//        mockMvc.perform(
//                        post("/api/v1/documents")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("""
//                                {}
//                                """)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(
//                        jsonPath("$.code")
//                                .value("DOCUMENT_VALIDATION_FAILED")
//                );
//
//        verifyNoInteractions(ingestionService);
//    }
//
//    @Test
//    void shouldRejectMissingContent() throws Exception {
//
//        mockMvc.perform(
//                        post("/api/v1/documents")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("""
//                                {
//                                  "name": "sample.txt"
//                                }
//                                """)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(
//                        jsonPath("$.code")
//                                .value("DOCUMENT_VALIDATION_FAILED")
//                );
//
//        verifyNoInteractions(ingestionService);
//    }
//
//    @Test
//    void shouldRejectMissingName() throws Exception {
//
//        mockMvc.perform(
//                        post("/api/v1/documents")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content("""
//                                {
//                                  "content": "hello"
//                                }
//                                """)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(
//                        jsonPath("$.code")
//                                .value("DOCUMENT_VALIDATION_FAILED")
//                );
//
//        verifyNoInteractions(ingestionService);
//    }
//}
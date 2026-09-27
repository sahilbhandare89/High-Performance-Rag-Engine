package com.example.rag.ingestion.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
class DocumentChunkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldChunkDocument() throws Exception {

        String request = """
                {
                  "name": "sample.txt",
                  "content": "This is a test document."
                }
                """;

        mockMvc.perform(
                        post("/api/documents/chunks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chunks").isArray())
                .andExpect(jsonPath("$.chunks[0].content")
                        .exists())
                .andExpect(jsonPath("$.chunks[0].position")
                        .value(0));
    }
}
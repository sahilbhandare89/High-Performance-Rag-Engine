package com.example.rag.retrieval.api;

import com.example.rag.retrieval.appication.RetrievedChunk;
import com.example.rag.retrieval.appication.SemanticSearchRequest;
import com.example.rag.retrieval.appication.SemanticSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/search")
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;

    public SemanticSearchController(
            SemanticSearchService semanticSearchService
    ) {
        this.semanticSearchService = Objects.requireNonNull(
                semanticSearchService,
                "semanticSearchService must not be null"
        );
    }

    @PostMapping
    public List<RetrievedChunk> search(
            @RequestBody SemanticSearchRequest request
    ) {
        return semanticSearchService.search(request);
    }
}
package com.example.rag.vector.infrastracture;

import com.example.rag.vector.application.similarity.SimilarityCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStorageConfiguration {

    @Bean
    public SimilarityCalculator similarityCalculator() {
        return new SimilarityCalculator();
    }
}
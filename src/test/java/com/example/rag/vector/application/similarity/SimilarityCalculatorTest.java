package com.example.rag.vector.application.similarity;

import com.example.rag.embedding.domain.Embedding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimilarityCalculatorTest {

    private SimilarityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SimilarityCalculator();
    }

    @Test
    void shouldReturnOneForIdenticalVectors() {

        Embedding first =
                new Embedding(new float[]{1.0f, 0.0f});

        Embedding second =
                new Embedding(new float[]{1.0f, 0.0f});

        double similarity =
                calculator.cosineSimilarity(first, second);

        assertThat(similarity)
                .isEqualTo(1.0);
    }

    @Test
    void shouldReturnZeroForOrthogonalVectors() {

        Embedding first =
                new Embedding(new float[]{1.0f, 0.0f});

        Embedding second =
                new Embedding(new float[]{0.0f, 1.0f});

        double similarity =
                calculator.cosineSimilarity(first, second);

        assertThat(similarity)
                .isEqualTo(0.0);
    }

    @Test
    void shouldReturnMinusOneForOppositeVectors() {

        Embedding first =
                new Embedding(new float[]{1.0f, 0.0f});

        Embedding second =
                new Embedding(new float[]{-1.0f, 0.0f});

        double similarity =
                calculator.cosineSimilarity(first, second);

        assertThat(similarity)
                .isEqualTo(-1.0);
    }

    @Test
    void shouldRejectDifferentDimensions() {

        Embedding first =
                new Embedding(new float[]{1.0f, 0.0f});

        Embedding second =
                new Embedding(new float[]{1.0f, 0.0f, 0.0f});

        assertThatThrownBy(
                () -> calculator.cosineSimilarity(first, second)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedding dimensions must match");
    }

    @Test
    void shouldRejectZeroVector() {

        Embedding first =
                new Embedding(new float[]{0.0f, 0.0f});

        Embedding second =
                new Embedding(new float[]{1.0f, 0.0f});

        assertThatThrownBy(
                () -> calculator.cosineSimilarity(first, second)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Cosine similarity is undefined for zero vectors"
                );
    }

    @Test
    void shouldReturnOneForVectorsWithSameDirection() {

        Embedding first =
                new Embedding(new float[]{1.0f, 0.0f});

        Embedding second =
                new Embedding(new float[]{5.0f, 0.0f});

        double similarity =
                calculator.cosineSimilarity(first, second);

        assertThat(similarity)
                .isEqualTo(1.0);
    }
}
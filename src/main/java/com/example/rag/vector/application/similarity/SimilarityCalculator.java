package com.example.rag.vector.application.similarity;

import com.example.rag.embedding.domain.Embedding;

import java.util.Objects;

public class SimilarityCalculator {

    public double cosineSimilarity(
            Embedding first,
            Embedding second
    ) {

        Objects.requireNonNull(first, "first embedding must not be null");
        Objects.requireNonNull(second, "second embedding must not be null");

        float[] firstValues = first.values();
        float[] secondValues = second.values();

        if (firstValues.length != secondValues.length) {
            throw new IllegalArgumentException(
                    "Embedding dimensions must match"
            );
        }

        double dotProduct = 0.0;
        double firstMagnitudeSquared = 0.0;
        double secondMagnitudeSquared = 0.0;

        for (int i = 0; i < firstValues.length; i++) {

            double a = firstValues[i];
            double b = secondValues[i];

            dotProduct += a * b;
            firstMagnitudeSquared += a * a;
            secondMagnitudeSquared += b * b;
        }

        if (firstMagnitudeSquared == 0.0 ||
                secondMagnitudeSquared == 0.0) {

            throw new IllegalArgumentException(
                    "Cosine similarity is undefined for zero vectors"
            );
        }

        double firstMagnitude =
                Math.sqrt(firstMagnitudeSquared);

        double secondMagnitude =
                Math.sqrt(secondMagnitudeSquared);

        return dotProduct /
                (firstMagnitude * secondMagnitude);
    }
}
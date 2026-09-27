package com.example.rag.embedding.infrastructure.provider;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

public final class LocalEmbeddingProvider implements EmbeddingProvider {

    private final int dimensions;

    public LocalEmbeddingProvider(int dimensions) {
        if (dimensions <= 0) {
            throw new IllegalArgumentException(
                    "dimensions must be greater than zero"
            );
        }

        this.dimensions = dimensions;
    }

    @Override
    public Embedding embed(String text) {
        Objects.requireNonNull(text, "text must not be null");

        return new Embedding(generateVector(text));
    }

    @Override
    public List<Embedding> embedBatch(List<String> texts) {
        Objects.requireNonNull(texts, "texts must not be null");

        return texts.stream()
                .map(this::embed)
                .toList();
    }

    private float[] generateVector(String text) {
        byte[] hash = sha256(text);

        float[] vector = new float[dimensions];

        for (int i = 0; i < dimensions; i++) {
            int unsignedByte = hash[i % hash.length] & 0xFF;

            vector[i] = (unsignedByte / 127.5f) - 1.0f;
        }

        return vector;
    }

    private byte[] sha256(String text) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            return digest.digest(
                    text.getBytes(StandardCharsets.UTF_8)
            );

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is unavailable",
                    e
            );
        }
    }
}
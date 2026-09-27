package com.example.rag.embedding.infrastructure;

import com.example.rag.embedding.application.port.EmbeddingProvider;
import com.example.rag.embedding.domain.Embedding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public final class FakeEmbeddingProvider implements EmbeddingProvider {

    private static final int DIMENSIONS = 8;

    @Override
    public Embedding embed(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }

        return new Embedding(generateVector(text));
    }

    @Override
    public List<Embedding> embedBatch(List<String> texts) {
        if (texts == null) {
            throw new IllegalArgumentException("texts must not be null");
        }

        return texts.stream()
                .map(this::embed)
                .toList();
    }

    private float[] generateVector(String text) {
        byte[] hash = sha256(text);

        float[] vector = new float[DIMENSIONS];

        for (int i = 0; i < DIMENSIONS; i++) {
            int unsignedByte = hash[i] & 0xFF;

            vector[i] =
                    (unsignedByte / 127.5f) - 1.0f;
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
package com.example.rag.embedding.domain;

import java.util.Arrays;
import java.util.Objects;

public final class Embedding {

    private final float[] values;

    public Embedding(float[] values) {
        Objects.requireNonNull(values, "values must not be null");

        if (values.length == 0) {
            throw new IllegalArgumentException(
                    "embedding must contain at least one value"
            );
        }

        for (float value : values) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException(
                        "embedding values must be finite"
                );
            }
        }

        this.values = Arrays.copyOf(values, values.length);
    }

    public int dimensions() {
        return values.length;
    }

    public float[] values() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Embedding other)) {
            return false;
        }

        return Arrays.equals(values, other.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString() {
        return "Embedding{dimensions=" + values.length + '}';
    }
}
package com.example.rag.embedding;

import com.example.rag.embedding.domain.Embedding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmbeddingTest {

    @Test
    public void shouldValidVector(){
        Embedding embedding =
                new Embedding(new float[]{0.1f, 0.2f, 0.3f});

        assertEquals(3, embedding.dimensions());
    }

    @Test
    public void shouldAcceptNull(){

        assertThrows(
                NullPointerException.class,
                () -> new Embedding(null)
        );
    }

    @Test
    public void shouldAcceptEmptyVector(){

        assertThrows(
                IllegalArgumentException.class,
                () -> new Embedding(new float[0])
        );
    }

    @Test
    public void shouldBeInfinite(){

        assertThrows(
                IllegalArgumentException.class,
                () -> new Embedding(new float[]{Float.POSITIVE_INFINITY})
        );
    }

    @Test
    public void shouldDimensionsBeCorrect(){

        float[] source = {1.0f, 2.0f};

        Embedding embedding = new Embedding(source);

        source[0] = 999.0f;

        assertEquals(
                1.0f,
                embedding.values()[0]
        );
    }
}

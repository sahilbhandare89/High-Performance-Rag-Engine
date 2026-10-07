package com.example.rag.embedding.infrastructure.resilience;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExponentialBackoffWithJitterTest {

    @Test
    void shouldCalculateFirstAttemptWithJitter() {

        JitterRandom random = bound -> 100;

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        random
                );

        Duration result = strategy.calculate(1);

        assertThat(result)
                .isEqualTo(Duration.ofMillis(100));
    }

    @Test
    void shouldCalculateSecondAttemptWithJitter() {

        JitterRandom random = bound -> 300;

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        random
                );

        Duration result = strategy.calculate(2);

        assertThat(result)
                .isEqualTo(Duration.ofMillis(300));
    }

    @Test
    void shouldCalculateThirdAttemptWithJitter() {

        JitterRandom random = bound -> 700;

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        random
                );

        Duration result = strategy.calculate(3);

        assertThat(result)
                .isEqualTo(Duration.ofMillis(700));
    }

    @Test
    void shouldCapExponentialBackoffAtMaximum() {

        JitterRandom random = bound -> 1500;

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        random
                );

        Duration result = strategy.calculate(5);

        assertThat(result)
                .isEqualTo(Duration.ofMillis(1500));
    }

    @Test
    void shouldAllowMaximumJitterValue() {

        JitterRandom random = bound -> bound - 1;

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        random
                );

        Duration result = strategy.calculate(1);

        assertThat(result)
                .isEqualTo(Duration.ofMillis(200));
    }

    @Test
    void shouldAllowZeroJitter() {

        JitterRandom random = bound -> 0;

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        random
                );

        Duration result = strategy.calculate(3);

        assertThat(result)
                .isEqualTo(Duration.ZERO);
    }

    @Test
    void shouldCapLargeAttemptAtMaximumBackoff() {

        JitterRandom random = bound -> 1500;

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        random
                );

        Duration result = strategy.calculate(20);

        assertThat(result)
                .isEqualTo(Duration.ofMillis(1500));
    }

    @Test
    void shouldRejectZeroAttempt() {

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        bound -> 0
                );

        assertThatThrownBy(() -> strategy.calculate(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("attempt must be greater than zero");
    }

    @Test
    void shouldRejectNegativeAttempt() {

        ExponentialBackoffWithJitter strategy =
                new ExponentialBackoffWithJitter(
                        Duration.ofMillis(200),
                        Duration.ofSeconds(2),
                        bound -> 0
                );

        assertThatThrownBy(() -> strategy.calculate(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("attempt must be greater than zero");
    }
}
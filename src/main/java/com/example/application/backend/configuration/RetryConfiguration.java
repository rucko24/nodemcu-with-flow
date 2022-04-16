package com.example.application.backend.configuration;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 *
 */
@Component
public class RetryConfiguration {

    /**
     *
     *
     * @return Retry
     */
    public Retry getRetryConfiguraton() {
        return Retry.of("retry-config", RetryConfig
                .custom()
                .waitDuration(Duration.ofSeconds(5000))
                .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofMillis(500L), 2d))
                .maxAttempts(3)
                .build());
    }

}

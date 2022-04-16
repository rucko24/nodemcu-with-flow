package com.example.application.backend.configuration;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 *
 * Simple bulkhead config
 */
@Configuration
public class BulkHeadConfiguration {

    /**
     *
     * @return Bulkhead with config
     */
    @Bean
    public Bulkhead getBulkHeadConfig() {
        return Bulkhead.of("bulk-head", BulkheadConfig
                .custom()
                .writableStackTraceEnabled(true)
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofMillis(5))
                .build());
    }
}

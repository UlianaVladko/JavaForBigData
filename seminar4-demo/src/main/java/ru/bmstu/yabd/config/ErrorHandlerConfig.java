package ru.bmstu.yabd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class ErrorHandlerConfig {

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<String, Object> kafkaOps) {
        var recoverer = new DeadLetterPublishingRecoverer(kafkaOps);
        var backoff = new FixedBackOff(1000L, 3);
        return new DefaultErrorHandler(recoverer, backoff);
    }
}

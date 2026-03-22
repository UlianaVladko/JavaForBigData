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

    // TODO 5: Создайте @Bean метод errorHandler(KafkaOperations<String, Object> kafkaOps)
    //         Используйте DeadLetterPublishingRecoverer + FixedBackOff(1000L, 3)
    //         return new DefaultErrorHandler(recoverer, backoff);

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<String, Object> kafkaOps) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaOps);

        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}

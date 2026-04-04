package ru.bmstu.yabd.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic customersTopic() {
        return TopicBuilder.name("customers").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic enrichedOrdersTopic() {
        return TopicBuilder.name("enriched-orders").partitions(3).replicas(1).build();
    }
}

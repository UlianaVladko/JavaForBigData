package ru.bmstu.yabd.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    // TODO 3: Создайте @Bean метод ordersTopic()
    //         return TopicBuilder.name("orders").partitions(3).replicas(1).build();

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // TODO 4: Создайте @Bean метод productCountsTopic()
    //         return TopicBuilder.name("product-counts").partitions(3).replicas(1).build();

    @Bean
    public NewTopic productCountsTopic() {
        return TopicBuilder.name("product-counts")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

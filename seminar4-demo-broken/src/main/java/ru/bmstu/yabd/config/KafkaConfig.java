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
        return TopicBuilder
                .name("orders")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // TODO 4: Создайте @Bean метод dltTopic()
    //         return TopicBuilder.name("orders.DLT").partitions(1).replicas(1).build();

    @Bean
    public NewTopic dltTopic() {
        return TopicBuilder
                .name("orders.DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

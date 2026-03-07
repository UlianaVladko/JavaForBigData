package ru.bmstu.yabd.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // TODO 2: Объявите @Bean-метод ordersTopic(), который создаёт топик "orders"
    //   с 3 партициями и 1 репликой.
    //   Подсказка: TopicBuilder.name("orders").partitions(3).replicas(1).build()

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // TODO 3: Объявите @Bean-метод paymentsTopic(), который создаёт топик "payments"
    //   с 3 партициями и 1 репликой.
    //   (аналогично ordersTopic, но name = "payments")

    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name("payments")
                .partitions(3)
                .replicas(1)
                .build();
    }
}

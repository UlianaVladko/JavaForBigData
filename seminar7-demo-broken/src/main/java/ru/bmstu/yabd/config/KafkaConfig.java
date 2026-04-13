package ru.bmstu.yabd.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    // TODO 2: Создайте бин NewTopic для топика "orders" (3 партиции, 1 реплика)
    // Подсказка: используйте new NewTopic("orders", 3, (short) 1)
    @Bean
    public NewTopic ordersTopic() {
        return new NewTopic("orders.v1", 3, (short) 1);
    }

    @Bean
    public NewTopic ordersV2Topic() {
        return new NewTopic("orders.v2", 3, (short) 1);
    }

//    @Bean
//    public NewTopic ordersDlqTopic() {
//        return new NewTopic("orders.dlq", 1, (short) 1);
//    }

}

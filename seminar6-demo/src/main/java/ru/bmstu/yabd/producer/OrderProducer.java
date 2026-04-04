package ru.bmstu.yabd.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.bmstu.yabd.model.Customer;
import ru.bmstu.yabd.model.OrderEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrder(OrderEvent event, String customerId) {
        log.info("Sending order to Kafka: {} (customer: {})", event, customerId);
        kafkaTemplate.send("orders", customerId, event);
    }

    public void sendCustomer(String customerId, Customer customer) {
        log.info("Sending customer to Kafka: {} -> {}", customerId, customer);
        kafkaTemplate.send("customers", customerId, customer);
    }
}

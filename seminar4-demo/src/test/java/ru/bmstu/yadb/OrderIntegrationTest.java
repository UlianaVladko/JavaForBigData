package ru.bmstu.yabd;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import ru.bmstu.yabd.model.OrderEvent;
import ru.bmstu.yabd.producer.OrderProducer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"orders"})
class OrderIntegrationTest {

    @Autowired
    private OrderProducer producer;

    @Test
    void shouldSendAndConsumeOrder() {

        String orderId = "TEST-1";

        OrderEvent event = new OrderEvent(
                orderId,
                "Test",
                10,
                1299.99,
                "Moscow"
        );

        // отправляем сообщение
        producer.send(event, "trace-test");

        // consumer настройки
        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps("test-group", "false", "embedded-kafka");

        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // десериализатор (ВАЖНО)
        org.springframework.kafka.support.serializer.JsonDeserializer<OrderEvent> deserializer =
                new org.springframework.kafka.support.serializer.JsonDeserializer<>(OrderEvent.class);
        deserializer.addTrustedPackages("*");

        DefaultKafkaConsumerFactory<String, OrderEvent> cf =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        deserializer
                );

        Consumer<String, OrderEvent> consumer = cf.createConsumer();
        consumer.subscribe(Collections.singletonList("orders"));

        // ждём сообщение
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {

                    ConsumerRecords<String, OrderEvent> records =
                            consumer.poll(Duration.ofMillis(100));

                    assertFalse(records.isEmpty(), "No messages in Kafka");

                    boolean found = records.iterator().hasNext() &&
                            records.iterator().next().value().orderId().equals(orderId);

                    assertTrue(found, "Order not found in Kafka");
                });

        consumer.close();
    }
}
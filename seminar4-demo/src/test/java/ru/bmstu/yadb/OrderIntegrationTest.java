package ru.bmstu.yabd;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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

//         отправляем сообщение
        producer.send(event, "trace-test");

//         создаём consumer для проверки
        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps("test-group", "false", "embedded-kafka");

        DefaultKafkaConsumerFactory<String, OrderEvent> cf =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        new org.springframework.kafka.support.serializer.JsonDeserializer<>()
                );

        Consumer<String, OrderEvent> consumer = cf.createConsumer();
        consumer.subscribe(Collections.singletonList("orders"));

//         ждём сообщение
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {

                    ConsumerRecords<String, OrderEvent> records =
                            consumer.poll(Duration.ofMillis(100));

                    assertFalse(records.isEmpty(), "No messages in Kafka");

                    boolean found = false;

                    for (ConsumerRecord<String, OrderEvent> record : records) {
                        if (orderId.equals(record.value().orderId())) {
                            found = true;
                        }
                    }

                    assertTrue(found, "Order not found in Kafka");
                });

        consumer.close();
    }
}
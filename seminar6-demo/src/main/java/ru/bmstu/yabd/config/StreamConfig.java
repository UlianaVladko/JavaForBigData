package ru.bmstu.yabd.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.bmstu.yabd.model.Customer;
import ru.bmstu.yabd.model.EnrichedOrder;
import ru.bmstu.yabd.model.OrderEvent;

import java.time.Duration;

@Configuration
public class StreamConfig {

    @Bean
    public KStream<String, OrderEvent> ordersPipeline(StreamsBuilder builder) {
        JsonSerde<OrderEvent> orderSerde = new JsonSerde<>(OrderEvent.class);
        orderSerde.configure(java.util.Map.of("spring.json.trusted.packages", "*"), false);

        JsonSerde<Customer> customerSerde = new JsonSerde<>(Customer.class);
        customerSerde.configure(java.util.Map.of("spring.json.trusted.packages", "*"), false);

        JsonSerde<EnrichedOrder> enrichedSerde = new JsonSerde<>(EnrichedOrder.class);
        enrichedSerde.configure(java.util.Map.of("spring.json.trusted.packages", "*"), false);

        // 1. Read orders stream
        KStream<String, OrderEvent> orders = builder.stream(
            "orders", Consumed.with(Serdes.String(), orderSerde));

        // 2. Count orders per product (for Interactive Queries)
        orders.filter((key, order) -> order != null && order.price() > 0)
              .groupBy((key, order) -> order.product(), Grouped.with(Serdes.String(), orderSerde))
              .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("product-counts")
                  .withKeySerde(Serdes.String())
                  .withValueSerde(Serdes.Long()));

        // 3. Read customers table
        KTable<String, Customer> customers = builder.table(
            "customers", Consumed.with(Serdes.String(), customerSerde));

        // 4. Join + fraud detection
        KStream<String, EnrichedOrder> enrichedStream =
                orders.join(
                        customers,
                        (order, customer) -> {
                            boolean isFraud = order.price() > 2000;

                            return new EnrichedOrder(
                                    order.orderId(),
                                    order.product(),
                                    order.quantity(),
                                    order.price(),
                                    customer.name(),
                                    isFraud
                            );
                        },
                        Joined.with(Serdes.String(), orderSerde, customerSerde)
                );

        // send to Kafka topic
        enrichedStream.to("enriched-orders",
                Produced.with(Serdes.String(), enrichedSerde));

        // 5. Fraud store (KeyValueStore)
        enrichedStream
                .filter((key, order) -> order.isFraud())
                .selectKey((key, order) -> order.orderId())
                .groupByKey(Grouped.with(Serdes.String(), enrichedSerde))
                .reduce(
                        (oldValue, newValue) -> newValue,
                        Materialized.<String, EnrichedOrder, KeyValueStore<Bytes, byte[]>>as("fraud-orders")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(enrichedSerde)
                );

        // 6. Trending products (KeyValueStore instead of window store)
        orders
                .filter((key, order) -> order != null && order.price() > 0)
                .groupBy((key, order) -> order.product(),
                        Grouped.with(Serdes.String(), orderSerde))
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("trending-products")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()));

        // 7. Revenue per product (KeyValueStore)
        orders
                .filter((key, order) -> order != null && order.price() > 0)
                .groupBy((key, order) -> order.product(),
                        Grouped.with(Serdes.String(), orderSerde))
                .aggregate(
                        () -> 0.0,
                        (product, order, agg) -> agg + order.price(),
                        Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as("revenue-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Double())
                );

        return orders;
    }
}

package ru.bmstu.yabd.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.bmstu.yabd.model.OrderEvent;

import java.time.Duration;

@Configuration
public class StreamConfig {

    // TODO 5: Создайте @Bean метод analyticsStream(StreamsBuilder builder)
    //         который возвращает KStream<String, OrderEvent>

    @Bean
    public KStream<String, OrderEvent> analyticsStream(StreamsBuilder builder) {
        JsonSerde<OrderEvent> orderSerde = new JsonSerde<>(OrderEvent.class);
        orderSerde.configure(java.util.Map.of(
                "spring.json.trusted.packages", "*",
                "spring.json.value.default.type", "ru.bmstu.yabd.model.OrderEvent"
        ), false);

        // TODO 6: Внутри метода создайте KStream из топика "orders"
        //         с правильными Serde (Serdes.String(), JsonSerde<OrderEvent>)

        KStream<String, OrderEvent> stream = builder.stream(
                "orders",
                Consumed.with(Serdes.String(), orderSerde)
        );

        // TODO 7: Постройте цепочку: filter(price > 0) → groupBy(product) →
        //         windowedBy(5 минут) → count() → toStream() → map() → to("product-counts")

        // TODO 8: Не забудьте Materialized.as("product-window-counts") для state store

        stream
                .filter((key, order) -> order != null && order.price() > 0)
                .groupBy(
                        (key, order) -> order.product(),
                        Grouped.with(Serdes.String(), orderSerde)
                )
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .count(Materialized.<String, Long, WindowStore<Bytes, byte[]>>as("product-window-counts")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()))
                .toStream()
                .map((windowedKey, count) ->
                        KeyValue.pair(windowedKey.key(), count.toString()))
                .to("product-counts", Produced.with(Serdes.String(), Serdes.String()));

        return stream;
    }
}
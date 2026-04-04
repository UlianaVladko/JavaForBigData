package ru.bmstu.yabd.controller;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.StoreQueryParameters;
import org.springframework.web.bind.annotation.*;

import ru.bmstu.yabd.service.StreamsService;

import java.util.*;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    private final KafkaStreams kafkaStreams;

    public RevenueController(StreamsService streamsService) {
        this.kafkaStreams = streamsService.getKafkaStreams();
    }

    @GetMapping
    public Map<String, Object> getRevenue() {

        ReadOnlyKeyValueStore<String, Double> store =
                kafkaStreams.store(
                        StoreQueryParameters.fromNameAndType(
                                "revenue-window-store",
                                QueryableStoreTypes.keyValueStore()
                        )
                );

        Map<String, Object> result = new HashMap<>();

        try (var iterator = store.all()) {
            while (iterator.hasNext()) {
                var entry = iterator.next();
                result.put(entry.key, entry.value);
            }
        }

        return result;
    }
}
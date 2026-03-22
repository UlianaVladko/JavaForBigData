package ru.bmstu.yabd.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import java.util.Map;

public class RegionPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        // TODO 2: Реализуйте партиционирование по ключу (региону).
        //         Используйте: Math.abs(key.toString().hashCode()) % numPartitions
        //         Не забудьте обработать случай key == null

        int numPartitions = cluster.partitionCountForTopic(topic);
        if (key == null) {
            return 0;
        }
        return Math.abs(key.toString().hashCode()) % numPartitions;
    }

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}
}

package com.slack.slack.common.messageBroker.kafka.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RebalanceListener implements ConsumerRebalanceListener {
    private final KafkaConsumer kafkaConsumer;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffset;

    // 리밸런싱이 일어나기 전에
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        log.warn("partitions are revoked");
        kafkaConsumer.commitSync(currentOffset);
    }

    // 리밸런싱이 끝나고
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        log.warn("partition are assigned");
    }
}
